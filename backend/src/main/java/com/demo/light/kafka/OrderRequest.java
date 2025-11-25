package com.demo.light.kafka;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
        private Long userId;
        private Long productId;
        private Integer quantity;


}
