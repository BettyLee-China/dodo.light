package com.demo.light.service.Impl;

import com.demo.light.bean.*;
import com.demo.light.bean.VO.CartItemVO;
import com.demo.light.bean.VO.CartVO;
import com.demo.light.repository.OrderItemMapper;
import com.demo.light.result.R;
import com.demo.light.service.CartService;
import com.demo.light.service.MinioService;
import com.demo.light.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate<String,CartItem> redisTemplate;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private MinioService minioService;
    @Autowired
    private OrderItemMapper orderItemMapper;
    private static final Logger log= LoggerFactory.getLogger(CartServiceImpl.class);
    public static final String CART_PREFIX="cart:";

    public String getCartKey(Long userId){
        return CART_PREFIX+userId;
    }

    @Override
    public R<Object> addItem(Long userId, Long productId, Integer quantity) {
        //判断参数是否合法
        if (productId == null||quantity==null||quantity<=0) {
            return R.builder().code(400).msg("参数错误").build();
        }

        String cartKey=getCartKey(userId);
        String field=productId.toString();

        Photo photo=photoService.selectById(productId);

        if (photo == null) {
            return R.builder().code(400).msg("商品不存在").build();
        }
        if (photo.getStock()<quantity) {
            return R.builder().code(400).msg("库存不足").build();
        }

        Object exisiting=redisTemplate.opsForHash().get(cartKey,field);
        CartItem cartItem;
        if (exisiting != null) {
            cartItem =(CartItem) exisiting;
            int newQty=cartItem.getQuantity()+quantity;
            if (newQty >99) {
                return R.builder().code(400).msg("单商品最多添加99件").build();
            }
            cartItem.setQuantity(newQty);
        }else {
            cartItem=new CartItem(quantity,true, LocalDateTime.now());
        }
        redisTemplate.opsForHash().put(cartKey,field,cartItem);
        redisTemplate.expire(cartKey,30,TimeUnit.DAYS);
        redisTemplate.opsForHash().put(cartKey, field, cartItem);
        Object verify = redisTemplate.opsForHash().get(cartKey, field);
        log.info("验证写入结果: {}", verify); // 看是否为 null 或内容正确
        return R.OK();
    }
    //这个代码运行下来是赋值
    @Override
    public R<Object> updateItem(Long userId, Long productId, Integer quantity, Boolean selected) {
        String cartKey=getCartKey(userId);
        String field=productId.toString();

        CartItem cartItem=(CartItem) redisTemplate.opsForHash().get(cartKey,field);
        if (cartItem == null) {
            return R.builder().code(400).msg("购物车中无此商品").build();
        }

        boolean updated=false;
        if (quantity != null && quantity>0) {
            Photo photo=photoService.selectById(productId);
            if (photo != null && photo.getStock()<quantity) {
                return R.builder().code(400).msg("库存不足").build();
            }
            cartItem.setQuantity(quantity);
            updated=true;
        }

        if (selected != null) {
            cartItem.setSelected(selected);
            updated=true;
        }
        if (updated) {
            redisTemplate.opsForHash().put(cartKey,field,cartItem);
            redisTemplate.expire(cartKey,30,TimeUnit.DAYS);
        }
        return R.OK();
    }



    @Override
    public CartVO getCart(Long userId) {
        String cartKey=getCartKey(userId);
        Map<Object,Object> entries=redisTemplate.opsForHash().entries(cartKey);

        Map<String, CartItemVO> items=new HashMap<>();
        Map<String,String> urlMap=new HashMap<>();
        int totalCount=0;
        BigDecimal totalAmount=BigDecimal.ZERO;

        for (Map.Entry<Object,Object> entry:entries.entrySet()){
            String productIdStr=(String) entry.getKey();
            CartItem item=(CartItem) entry.getValue();
            if (Boolean.TRUE.equals(item.getSelected())) {
                Photo photo =
                        photoService.selectById(Long.valueOf(productIdStr));
                if (photo != null) {
                    BigDecimal price = photo.getPrice();
                    String imageUrl = minioService.getPresignedUrl(photo.getObjectName(), Duration.ofDays(7));
                    urlMap.put(productIdStr, imageUrl);
                    totalCount += item.getQuantity();
                    totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));


                    CartItemVO itemVO = CartItemVO.builder()
                            .objectName(urlMap.get(productIdStr))
                            .productName(photo.getTitle())
                            .ProductId(photo.getId())
                            .quantity(item.getQuantity())
                            .selected(item.getSelected())
                            .currentPrice(photo.getPrice())
                            .updateTime(photo.getUploadTime())
                            .build();
                    items.put(productIdStr, itemVO);
                }
            }
        }


        CartVO cart=CartVO.builder()
                .items(items)
                .totalCount(totalCount)
                .totalAmount(totalAmount)
                .updateTime(LocalDateTime.now())
                .build();

        return cart;
    }

    @Override
    public void deleteItems(Long userId, List<Long> productIds) {

        String cartKey=getCartKey(userId);
        for (Long pid:productIds){
            redisTemplate.opsForHash().delete(cartKey,pid.toString());
        }

    }
    //删除某一项 删除成功 注意要先把Long类型转换了

    @Override
    public R<Object> deleteItem(Long userId, Long productId) {
        String cartKey=getCartKey(userId);
        redisTemplate.opsForHash().delete(cartKey,productId.toString());
        return null;
    }

    @Override
    public R<Object> clearCart(Long userId) {
        String cartKey=getCartKey(userId);
        redisTemplate.delete(cartKey);
        return R.OK();
    }


}
