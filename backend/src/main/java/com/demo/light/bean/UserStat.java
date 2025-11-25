package com.demo.light.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserStat {
    private Long id;
    private Long userId;
    private Integer followCount;
    private Integer followerCount;
    public UserStat(Long userId){
        this.userId=userId;
    }
}
