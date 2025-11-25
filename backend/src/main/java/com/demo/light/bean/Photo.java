package com.demo.light.bean;

import com.demo.light.enums.PhotoMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Photo{
     private Long id;
     private String title;
     //这个是url
     private String objectName;
     private String description;
     private BigDecimal price;
     private PhotoMode photoMode;
     private LocalDateTime uploadTime;
     private Long photographerId;
     //理论上没有库存，但是可以是一种商业手段
     private Integer stock;
     private String cameraModel;
     private String location;
     private String exposureInfo;



}
