package com.demo.light.bean.VO;

import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemVO {
    private Long ProductId;
    private String productName;
    private String objectName;
    private BigDecimal currentPrice;

    private Integer quantity;
    private Boolean selected;

    private LocalDateTime addedTime;
    private LocalDateTime updateTime;
}
