package com.demo.light.bean.VO;

import com.demo.light.bean.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoVO {
    //商品id（这里是照片）
    private Long id;
    private String name;
    //照片图片的url,完整的
    private String url;
    private String brief;
    private BigDecimal price;
    //市场原价，用于划线展示
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer status;
    private LocalDateTime updateTime;
    private Integer soldCount;

    //商户的信息
    private UserProfileVO userProfileVO;

}
