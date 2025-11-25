package com.demo.light.bean.VO;

import com.demo.light.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVO {
    private String orderId;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private OrderStatus orderStatus;
}
