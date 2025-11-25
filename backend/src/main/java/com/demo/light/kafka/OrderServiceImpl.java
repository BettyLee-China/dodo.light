package com.demo.light.kafka;

import com.demo.light.bean.*;
import com.demo.light.bean.OrderRequest;
import com.demo.light.bean.VO.OrderDetailVO;
import com.demo.light.bean.VO.OrderResponse;
import com.demo.light.bean.VO.OrderVO;
import com.demo.light.converter.OrderConverter;
import com.demo.light.enums.OrderStatus;
import com.demo.light.repository.OrderItemMapper;
import com.demo.light.repository.OrderMapper;
import com.demo.light.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private MinioService minioService;


    //创建订单
    @Override
    public Order createOrder(Long userId, OrderRequest request) {
        if (request.getItems()==null ||request.getItems().isEmpty()){
            throw new IllegalArgumentException("商品订单不能为空");
        }
        //获取地址
        Address address=addressService.getAddressById(request.getAddressId(),userId);

        List<ProductSnapshot> shots=productService.getProductSnapshot(
                request.getItems().stream().map(OrderItemRequest::getProductId).collect(Collectors.toList())
        );

        List<OrderItem> orderItems=new ArrayList<>();
        BigDecimal totalPrice=BigDecimal.ZERO;

        for (OrderItemRequest item: request.getItems()){
            ProductSnapshot snapshot=shots.stream()
                    .filter(p->p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(()->new RuntimeException("商品不存在："+item.getProductId()));

            if (snapshot.getStock()<item.getQuantity()){
                throw new RuntimeException("库存不足："+snapshot.getProductName());
            }
            String image = minioService.getPresignedUrl(snapshot.getObjectName(), Duration.ofDays(7));

            OrderItem orderItem=OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(snapshot.getProductName())
                    .productImage(image)
                    .unitPrice(snapshot.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(snapshot.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();

            orderItems.add(orderItem);
            totalPrice=totalPrice.add(orderItem.getTotalPrice());
        }

        //计算运费、优惠

        BigDecimal freightAmount = calculateFreight(address);       // 示例：满99包邮
        BigDecimal discountAmount = BigDecimal.ZERO;                // 后续可接入优惠券
        BigDecimal payAmount = totalPrice.add(freightAmount).subtract(discountAmount);


        //创建订单主表
        String orderId="ORD-"+System.currentTimeMillis();
        Order order=Order.builder()
                .orderId(orderId)
                .userId(userId)
                .totalAmount(totalPrice)
                .freightAmount(freightAmount)
                .discountAmount(discountAmount)
                .payAmount(payAmount)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .createTime(LocalDateTime.now())
                .build();

        //保存到数据库
        orderMapper.insertOrder(order);
        for (OrderItem item:orderItems){
            item.setOrderId(orderId);
            orderItemMapper.insertOrderItem(item);
        }

        //发送事件
        kafkaProducer.sendOrderEvent("ORDER_CREATED",order);
        return order;
    }

    //一键购买

    @Override
    public Order createOrder(Long userId, Integer addressId, Long productId, Integer quantity) {
        //获取地址
        Address address=addressService.getAddressById(addressId,userId);

        //获取产品快照
        ProductSnapshot ps = productService.getProductSnapshot(productId);

        BigDecimal totalPrice=BigDecimal.ZERO;

        if(ps.getStock()<quantity){
            throw  new RuntimeException("库存不足"+ps.getProductName());
        }



        totalPrice=ps.getPrice().multiply(new BigDecimal(quantity));
        String orderId="ORD-"+System.currentTimeMillis();

        //orderItem
        OrderItem orderItem=OrderItem.builder()
                .orderId(orderId)
                .productId(productId)
                .productName(ps.getProductName())
                .productImage(ps.getObjectName())
                .unitPrice(ps.getPrice())
                .quantity(quantity)
                .totalPrice(totalPrice)
                .build();

        orderItemMapper.insertOrderItem(orderItem);

        //创建主表
        Order order=Order.builder()
                .orderId(orderId)
                .userId(userId)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .createTime(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .totalAmount(totalPrice)
                .payAmount(totalPrice)
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .build();
        orderMapper.insertOrder(order);

        kafkaProducer.sendOrderEvent("ORDER_CREATED",order);
        return order;

    }

    //预览订单
    @Override
    public OrderDetailVO previewOrder(String orderId) {
        Order order=orderMapper.selectOrderByOrderNo(orderId);

        List<OrderItem> items=orderItemMapper.selectOrderItemByOrderId(orderId);
        OrderDetailVO vo= OrderConverter.INSTANCE.convertToVO(order);
        if (vo == null) {
            return null;
        }
        vo.setItems(items);
        return vo;
    }
    // 简单运费规则：满99免运费，否则10元
    private BigDecimal calculateFreight(Address address) {
        // 实际项目中可根据地区、重量等计算
        return BigDecimal.TEN; // 暂定10元
    }

    @Override
    public void processPaymentSuccess(String orderId) {
        //查询order
        Order order = orderMapper.selectOrderByOrderNo(orderId);
        if (order == null ||OrderStatus.PAID.equals(order.getOrderStatus())){
            return;//已经处理过的，不用再管
        }

        //扣减真实的库存
        //先要获取到items
        List<Long> productIds=new ArrayList<>();

        List<OrderItem> items = orderItemMapper.selectOrderItemByOrderId(orderId);
        for (OrderItem item:items){
            Long id = item.getId();
            photoService.decreaseStock(id, item.getQuantity());
            productIds.add(id);
        }

        //清除购物车中的相关物品
        cartService.deleteItems(order.getUserId(), productIds);


        order.setPayTime(LocalDateTime.now());
        orderMapper.updatePayTime(orderId,LocalDateTime.now());


        //发送支付成功事件到kafka
        kafkaProducer.sendOrderEvent("ORDER_PAID",order);
    }

    @Override
    public Order getOrderByOrderNo(String orderId) {
        return orderMapper.selectOrderByOrderNo(orderId);
    }

    @Override
    public void handleRefundSuccess(String orderId, String outRequestNo, String refundAmount, Date gmtRefundPay){

        Order order=orderMapper.selectOrderByOrderNo(orderId);
        if (order == null) {
            return;
        }

        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setRefundAmount(new BigDecimal(refundAmount));
        order.setRefundTime(gmtRefundPay.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        order.setOutRequestNo(outRequestNo);
        orderMapper.updateById(order);
        // TODO: 发消息：order.refunded
        // kafkaTemplate.send("order.refunded", orderNo);
    }
    @Transactional
    @Override
    public void setOrderStatus(String orderId, OrderStatus orderStatus) {
        orderMapper.updateOrderStatus(orderId, orderStatus);
    }


    //获取具体的order包括items
    @Override
    public OrderDetailVO getOrderDetail(String orderId) {
        //先查一下order
        Order order = orderMapper.selectOrderByOrderNo(orderId);

        //再查一下orderItem
        List<OrderItem> items = orderItemMapper.selectOrderItemByOrderId(orderId);

        //再查一下地址address


        OrderDetailVO vo=OrderDetailVO.builder()
                .items(items)
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .receiverPhone(order.getReceiverPhone())
                .province(order.getProvince())
                .city(order.getCity())
                .district(order.getDistrict())
                .detailAddress(order.getDetailAddress())
                .payTime(order.getPayTime())
                .build();


        return vo;
    }

    @Override
    public List<OrderVO> getOrders(Long userId) {
        List<Order> orderList = orderMapper.selectOrdersByUserId(userId);

        List<OrderVO> voList=new ArrayList<>();
        for (Order order:orderList){
            //先拿到每个order的orderItem
            //这里肯定是做复杂了，因为底层是电商的那种，而不是标准的单体购买。
            //实际上这里的item只能有一样商品
            List<OrderItem> items = orderItemMapper.selectOrderItemByOrderId(order.getOrderId());
            for (OrderItem item:items){
                //拿到商品的名字
                String productName=item.getProductName();
                //拿到商品的图片
                String productImage=item.getProductImage();
                String url = minioService.getPresignedUrl(productImage, Duration.ofDays(7));
                OrderVO vo=OrderVO.builder()
                        .createTime(order.getCreateTime())
                        .orderId(order.getOrderId())
                        .orderStatus(order.getOrderStatus())
                        .imageUrl(url)
                        .productName(productName)
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .payTime(order.getPayTime())
                        .build();
                voList.add(vo);
            }

        }
        return voList;
    }
}
