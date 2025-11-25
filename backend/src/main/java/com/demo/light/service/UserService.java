package com.demo.light.service;

import com.demo.light.bean.*;
import com.demo.light.bean.DTO.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    PureUser selectByUsername(String username);

    PureUser selectByUserId(Long userId);
    PureUser selectByProviderAndProviderId(String provider,Long providerId);
    int insertOneUser(PureUser user);

    List<String> selectPermissionsByUsername(String username);


    //关于UserProfiles的相关操作
    //注册填表
    int insertUserProfileAuto(UserProfile userProfile);
    //修改信息
    int updateUserProfile(UserProfile userProfile,Long userId);

    int uploadAvatar(String avatar,Long userId);

    UserProfile getProfile(Long userId);

    //关于UserStats的相关操作
    //根据userId查询信息
    UserStat findUserStatByUserId(Long userId);

    int initUserStat(UserStat userStat);

}
