package com.demo.light.bean.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PhotoUploadDTO {

    @Schema(description = "标题", example = "风景照")
    private String title;

    @Schema(description = "描述", example = "美丽的山水")
    private String description;

    @Schema(description = "拍摄模式", example = "LANDSCAPE")
    private String photoMode;

    @Schema(description = "价格", example = "9.90")
    private BigDecimal price;

    @Schema(description = "摄影师ID", example = "123")
    private Long photographerId;

    @Schema(description = "库存", example = "100")
    private Integer stock;
}