package com.demo.light.bean;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSnapshot {
    private Long productId;
    private String productName;
    private String objectName;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
}
