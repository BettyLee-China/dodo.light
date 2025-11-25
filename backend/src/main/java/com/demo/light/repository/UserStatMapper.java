package com.demo.light.repository;

import com.demo.light.bean.UserStat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserStatMapper {
    UserStat selectUserStatByUserId(Long userId);

    int insertUserStat(UserStat userStat);

}
