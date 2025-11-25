package com.demo.light.controller;

import com.demo.light.annotation.CurrentUser;
import com.demo.light.bean.DTO.MultipartPhotoRequest;
import com.demo.light.bean.DTO.WithdrawRequest;
import com.demo.light.bean.Photo;
import com.demo.light.bean.DTO.PhotoDto;
import com.demo.light.bean.DTO.PhotoUploadDTO;
import com.demo.light.bean.User;
import com.demo.light.enums.PhotoMode;
import com.demo.light.result.R;
import com.demo.light.service.MinioService;
import com.demo.light.service.PhotoService;
import com.demo.light.service.UserService;
import com.demo.light.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;



@RestController
@RequestMapping("/photographer")
public class PhotographerController {
    @Autowired
    private PhotoService photoService;
    @Autowired
    private MinioService minioService;
    @Autowired
    private UserService userService;
    @Autowired
    private WithdrawalService withdrawalService;



    //查询作品集通过摄影师的id
    @GetMapping("/portfolios/{photographerId}")
    public R<Object> getPortfolios(@PathVariable Long photographerId){
       List<Photo> photos= photoService.selectByPhotographerId(photographerId);
        return R.builder().data(photos).code(200).msg("获取作品集成功").build();
    }


    //传到作品集 这个接口成功了
    @Operation(summary = "摄影师上传作品",description = "上传一张图片及其信息")
    @RequestBody(
            description = "作品信息及图片文件",
            required = true,

            content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(implementation = MultipartPhotoRequest.class), // 👈 引用 DTO
                    encoding = {
                            @Encoding(name = "image", contentType = "image/jpeg, image/png, image/gif"),
                            @Encoding(name = "metadata", contentType = "application/json")
                    }
            )
    )
    @PostMapping(value = "/post",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Object> postPhoto(
            @RequestPart("image") MultipartFile image,
    @RequestPart("metadata") PhotoUploadDTO metadata){
//                               @RequestPart("title") String title,
//                               @RequestPart("description") String description,
//                               @RequestPart("photoMode") PhotoMode photoMode,
//                               @RequestPart("price") BigDecimal price,
//                               @RequestPart("photographerId") Long photographerId,
//                               @RequestPart Integer stock){

        try {
            //先上传到中间件，获取到objectName
            String objectName=minioService.uploadImage(image);

            Photo photo= Photo.builder()
                    .id(ThreadLocalRandom.current().nextLong()&Long.MAX_VALUE)
                    .title(metadata.getTitle())
                    .description(metadata.getDescription())
                    .price(metadata.getPrice())
                    .photoMode(PhotoMode.valueOf(metadata.getPhotoMode()))
                    .objectName(objectName)
                    .uploadTime(LocalDateTime.now())
                    .photographerId(metadata.getPhotographerId())
                    .stock(metadata.getStock())
                    .build();
            photoService.saveOnePhoto(photo);
            return R.builder().code(200).msg("上传作品成功").build();
        }catch (Exception e){
            e.printStackTrace();
            return R.builder().code(400).msg("上传失败").build();
        }


    }

//    查询某个单独的照片 测试成功 但是我在想 要不要只返回一个url呢？这样可能服务器会轻松一点吧
    @GetMapping("/image/{id}")
    public R<Object> getImage(@PathVariable Long id){
        Photo photo=photoService.selectById(id);
        PhotoDto photoDto=PhotoDto.builder()
                .title(photo.getTitle())
                .photoMode(photo.getPhotoMode())
                .price(photo.getPrice())
                .description(photo.getDescription())
                .build();

        // ✅ 动态生成最新的 presigned URL
        if (photo.getObjectName() != null) {
            try {
                String imageUrl = minioService.getPresignedUrl(
                        photo.getObjectName(),
                        Duration.ofDays(7)
                );
                photoDto.setImageUrl(imageUrl);
            } catch (Exception e) {
                photoDto.setImageUrl(null);
            }
        }
        return R.builder().data(photo).build();
    }

    //删除某个图片 测试失败
    @DeleteMapping("/deletePhoto/{id}")
    public R<Object> deletePhotoById(@PathVariable Long id){
        String objectName = photoService.selectById(id).getObjectName();
        int result= photoService.decreaseById(id);

        if (result == 1) {
            minioService.deleteImage(objectName);
            return R.builder().code(200).msg("删除成功").build();
        }
        return R.builder().code(400).msg("请求失败").build();
    }



}
