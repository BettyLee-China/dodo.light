package com.demo.light.controller;

import com.demo.light.bean.UserProfile;
import com.demo.light.bean.UserStat;
import com.demo.light.enums.CodeEnum;
import com.demo.light.enums.GenderEnum;
import com.demo.light.result.R;
import com.demo.light.service.MinioService;
import com.demo.light.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;


@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MinioService minioService;


    //注册详细信息，这一步是在登录之后会提示出来的。因为有一个用户就一定有一个profile。这是系统自动调用的。
    //后面的都是修改、或者获取了。
    @PutMapping(value = "/profile/modify/{userId}")
    public R<Object> modifyProfile(@RequestParam(required = false) String nickname,
                                   @RequestParam(required = false) LocalDate birthday,
                                   @RequestParam(required = false) String gender,
                                   @RequestParam(required = false) String bio,
                                   @PathVariable Long userId){
        UserProfile formData=UserProfile.builder()
                .bio(bio)
                .birthday(birthday)
                .nickname(nickname)
                .build();
        // 安全地设置 gender
        if (gender != null && !gender.trim().isEmpty()) {
            formData.setGender(GenderEnum.fromValue(gender)); // 只有非空才转换
        } // 否则保持 null 或默认值


        int i = userService.updateUserProfile(formData,userId);
        if (i==1) {
            UserProfile profile = userService.getProfile(userId);
            return R.builder().code(200).msg("修改成功").data(profile).build();
        }
        return R.FAIL(CodeEnum.INSERT_FAIL);
    }
    //修改头像需要走不同的通道
    @PostMapping("/profile/{userId}/upload-avatar")
    public  R<Object> modifyAvatar(@RequestParam("avatar")MultipartFile avatar,
                                   @PathVariable Long userId){
        //和上传图片的逻辑相同，要先上传到minIo，再生成objectName。
        String objectName = minioService.uploadImage(avatar);

        int i = userService.uploadAvatar(objectName,userId);
        if (i==1) {
            String url = minioService.getPresignedUrl(objectName, Duration.ofDays(7));
            return R.builder().code(200).msg("修改成功").data(url).build();
        }
        return R.FAIL(CodeEnum.INSERT_FAIL);
    }

    //获取stats
    @GetMapping("/stats/{userId}")
    public R<Object> getStats(@PathVariable Long userId){
        UserStat userStat = userService.findUserStatByUserId(userId);
        return R.builder().code(200).msg("获取数据成功！").data(userStat).build();
    }
    //获取用户头像和用户的昵称简介等
    @GetMapping("/profile/{userId}")
    public R<Object> getAvatar(@PathVariable Long userId){
        UserProfile profile = userService.getProfile(userId);
        String avatar = minioService.getPresignedUrl(profile.getAvatar(), Duration.ofDays(7));
        profile.setAvatar(avatar);
        return R.builder().code(200).msg("获取用户信息成功！").data(profile).build();
    }

    //初始化stat
}
