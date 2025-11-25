package com.demo.light.bean.VO;

import com.demo.light.bean.Address;
import com.demo.light.bean.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class OrderResponse {
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Address shippingAddress;
}
