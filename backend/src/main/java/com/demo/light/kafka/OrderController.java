package com.demo.light.kafka;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayAcquireRefundRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.demo.light.bean.Order;
import com.demo.light.bean.OrderRequest;
import com.demo.light.bean.VO.OrderVO;
import com.demo.light.enums.CodeEnum;
import com.demo.light.result.R;
import com.demo.light.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Resource
    private AlipayClient alipayClient;
    @Autowired
    private JwtUtil jwtUtil;




    @GetMapping("/preview/{orderId}")
    public R<Object> previewOrder(@PathVariable String orderId){
        return R.builder()
                .code(200)
                .msg("订单预览")
                .data(orderService.previewOrder(orderId))
                .build();
    }

    @PostMapping("/create")
    public R<Object> createOrder(@RequestBody OrderRequest orderRequest,
                                 HttpServletRequest request){
        String token=jwtUtil.getTokenFromRequest(request);
        Long userId=Long.valueOf(jwtUtil.parseToken(token));
        return R.builder().code(200).msg("创建订单成功")
                .data(orderService.createOrder(
                        userId,
                        orderRequest
                ))
                .build();
    }

    @PostMapping("/paid")
    public R<Object> paymentSuccess(@RequestParam String orderId){
        orderService.processPaymentSuccess(orderId);
        return R.builder().code(200).msg("支付成功").data(orderId).build();
    }


    @PostMapping("/refund")
    public R<Object> refund(@RequestBody AlipayAcquireRefundRequest request){
        try{
            Order order=orderService.getOrderByOrderNo(request.getOperatorId());
            if (order == null) {
                return R.builder().code(400).msg("订单不存在").build();
            }
            if (!"PAID".equals(order.getOrderStatus())) {
                return R.builder().code(400).msg("订单未支付，无法退款").build();
            }

            String outRequestNo=request.getOutRequestNo();
            if (outRequestNo == null||outRequestNo.isEmpty()) {
                outRequestNo=order.getOrderId()+"_R1";
            }

            AlipayTradeRefundRequest refundRequest=new AlipayTradeRefundRequest();
            refundRequest.setBizContent("{" +
                    "\"out_trade_no\":\"" + order.getOrderId() + "\"," +
                    "\"refund_amount\":" + request.getRefundAmount() + "," +
                    "\"out_request_no\":\"" + outRequestNo + "\"," +
                    "\"refund_reason\":\"" + (request.getRefundReason() == null ? "用户申请退款" : request.getRefundReason()) + "\"" +
                    "}"
            );

            AlipayTradeRefundResponse response=alipayClient.execute(refundRequest);

            if (response.isSuccess()){
                orderService.handleRefundSuccess(
                        order.getOrderId(),
                        outRequestNo,
                        request.getRefundAmount(),
                        response.getGmtRefundPay()
                );
                return R.OK();
            }else {
                return R.FAIL(CodeEnum.REFUND_FAIL);
            }
        }catch (AlipayApiException e){
            return R.FAIL(CodeEnum.REFUND_FAIL);
        }

    }


    //需要的到底是怎么样的order？如果还是需要那种需要的items的那种
    @GetMapping("/{orderId}/status")
    public R<Order> getOrderStatus(@PathVariable String orderId){
        Order order=orderService.getOrderByOrderNo(orderId);
        return R.OK(order);
    }
    //获得所有的订单列表
    @GetMapping("/get/{userId}")
    public R<List<OrderVO>> getOrders(@PathVariable Long userId){
        List<OrderVO> orders = orderService.getOrders(userId);
        return R.OK(orders);
    }

    //创建订单（一键购买）
    @PostMapping("/createOne")
    public R<Object> createOrder(HttpServletRequest request,
                           @RequestParam Integer addressId,
                           @RequestParam Long productId,
                           @RequestParam Integer quantity
                           ){

        String token=jwtUtil.getTokenFromRequest(request);
        Long userId=Long.valueOf(jwtUtil.parseToken(token));

        return R.builder().code(200).msg("创建订单成功")
                .data(orderService.createOrder(
                        userId,addressId,productId,quantity

                ))
                .build();

    }

}
