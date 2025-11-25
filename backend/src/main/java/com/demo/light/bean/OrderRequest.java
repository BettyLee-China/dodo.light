package com.demo.light.bean;


import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private Integer addressId;
    private List<OrderItemRequest> items;
}
