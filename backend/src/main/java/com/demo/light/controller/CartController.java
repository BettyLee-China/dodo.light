package com.demo.light.controller;

import com.demo.light.bean.VO.CartVO;
import com.demo.light.result.R;
import com.demo.light.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    CartService cartService;

    //拉取购物车
    @GetMapping("/{userId}")
    public R<CartVO> getCart(@PathVariable("userId") Long userId){
        CartVO carts = cartService.getCart(userId);

        return R.OK(carts);
    }

    //添加到购物车，这是增加
    @PostMapping("/add")
    public R<Object> addCartItem(@RequestParam("userId") Long userId,
                             @RequestParam("productId") Long productId,
                             @RequestParam("quantity") Integer quantity){
        cartService.addItem(userId, productId, quantity);
        return R.builder().code(200).msg("添加成功").build();
    }

    //更新购物车内的物品,本质上是赋值，前端先计算之后再发过来。
    @PostMapping("/update")
    public R<Object> removeItem(@RequestParam("userId") Long userId,
                                @RequestParam("productId") Long productId,
                                @RequestParam("quantity") Integer quantity){
       return cartService.updateItem(userId, productId, quantity, true);
    }

    //删除购物车内的物品(单个)
    @DeleteMapping("/remove/{userId}")
    public R<Object> deleteItem(@PathVariable("userId") Long userId,
                                @RequestParam("productId") Long productId){
      return  cartService.deleteItem(userId,productId);
    }

    //清空购物车
    @DeleteMapping("/delete/{userId}")
    public R<String> deleteItems(@RequestParam("userId") Long userId,
                                 @RequestParam("productIds")List<Long> productIds){
        cartService.deleteItems(userId, productIds);
        return R.OK("删除物品成功");
    }

    //清空购物车
    @DeleteMapping("/clear")
    public R<String> clear(){
        return R.OK("清空购物车");
    }

}
