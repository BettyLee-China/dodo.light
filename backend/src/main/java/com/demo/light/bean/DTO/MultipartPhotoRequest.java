package com.demo.light.bean.DTO;

// com.yourpackage.dto.MultipartPhotoRequest.java

import com.demo.light.bean.DTO.PhotoUploadDTO;
import io.swagger.v3.oas.annotations.media.Schema;

public class MultipartPhotoRequest {

    @Schema(description = "图片文件", type = "string", format = "binary")
    public Object image; // 注意：这里不能写 MultipartFile，要用 Object 或 byte[]

    @Schema(description = "作品元数据")
    public PhotoUploadDTO metadata;
}