package com.demo.light.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//dto
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
