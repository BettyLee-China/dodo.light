package com.demo.light.repository;

import com.demo.light.bean.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfilesMapper {

    int initUserProfile(UserProfile userProfile);

    //修改用户信息profile
    int modifyUserProfile(UserProfile userProfile,Long userId);

    //通过userId获取UserProfile
    UserProfile selectProfileByUserId(Long userId);

    //根据userId更新avatar
    int updateAvatarByUserId(String avatar,Long userId);

}
