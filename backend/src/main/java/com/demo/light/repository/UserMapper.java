package com.demo.light.repository;

import com.demo.light.bean.PureUser;
import com.demo.light.bean.User;
import com.demo.light.enums.UserTypeEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserMapper {



    PureUser findByProviderAndProviderId(String provider, Long providerId);

    PureUser findByUsername(String username);

    PureUser findByUserId(Long userId);




    int saveOneUser(PureUser user);

    Integer findUserIdByUsername(String username);

//    查询权限
   List<String> findPermissionsByUsername(String username);



}
