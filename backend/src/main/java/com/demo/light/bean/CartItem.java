package com.demo.light.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long id;
    private Integer quantity;
    private Long ProductId;
    private String productName;
    private String objectName;
    private BigDecimal currentPrice;
    private Boolean selected;
    private LocalDateTime addedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updateTime;
    private BigDecimal latestPrice;
    private String stockStatus;

    public CartItem(Integer quantity, Boolean selected, LocalDateTime addedAt) {
        this.quantity = quantity;
        this.selected = selected;
        this.addedAt = addedAt;
    }
}
