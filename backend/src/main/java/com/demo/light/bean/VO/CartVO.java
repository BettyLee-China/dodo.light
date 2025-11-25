package com.demo.light.bean.VO;

import com.demo.light.bean.VO.CartItemVO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartVO {
    private Map<String, CartItemVO> items;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private LocalDateTime updateTime;
}
