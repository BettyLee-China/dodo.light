package com.demo.light.kafka;

import lombok.*;

import com.demo.light.bean.Order;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {
    private String eventType;//"ORDER_CREATED","ORDER_PAID"
    private Order order;


}
