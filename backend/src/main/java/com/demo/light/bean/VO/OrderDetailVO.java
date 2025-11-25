package com.demo.light.bean.VO;


import com.demo.light.bean.OrderItem;
import com.demo.light.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailVO {
    private String orderId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private LocalDateTime createTime;
    private LocalDateTime payTime;

    private List<OrderItem> items;
}
