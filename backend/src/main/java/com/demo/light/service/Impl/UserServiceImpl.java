package com.demo.light.service.Impl;

import com.demo.light.bean.DTO.RegisterRequest;
import com.demo.light.bean.PureUser;
import com.demo.light.bean.User;
import com.demo.light.bean.UserProfile;
import com.demo.light.bean.UserStat;
import com.demo.light.repository.UserMapper;
import com.demo.light.repository.UserProfilesMapper;
import com.demo.light.repository.UserStatMapper;
import com.demo.light.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfilesMapper userProfilesMapper;
    @Autowired
    private UserStatMapper userStatMapper;


    @Override
    public List<String> selectPermissionsByUsername(String username) {
        return userMapper.findPermissionsByUsername(username);
    }


    @Override
   public PureUser selectByUsername(String username){
       return userMapper.findByUsername(username);
   }

    @Override
    public PureUser selectByUserId(Long userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public PureUser selectByProviderAndProviderId(String provider, Long providerId) {
        return userMapper.findByProviderAndProviderId(provider,providerId);
    }

    @Override
    public int insertOneUser(PureUser user) {
     return  userMapper.saveOneUser(user);
    }


    @Override
    public int insertUserProfileAuto(UserProfile userProfile) {
        return userProfilesMapper.initUserProfile(userProfile);
    }

    @Override
    public int updateUserProfile(UserProfile userProfile, Long userId) {
        return userProfilesMapper.modifyUserProfile(userProfile,userId);
    }

    @Override
    public int uploadAvatar(String avatar, Long userId) {
        return userProfilesMapper.updateAvatarByUserId(avatar, userId);
    }

    @Override
    public UserProfile getProfile(Long userId) {
        return userProfilesMapper.selectProfileByUserId(userId);
    }

    @Override
    public UserStat findUserStatByUserId(Long userId) {
        return userStatMapper.selectUserStatByUserId(userId);
    }

    @Override
    public int initUserStat(UserStat userStat) {
        return userStatMapper.insertUserStat(userStat);
    }

}
