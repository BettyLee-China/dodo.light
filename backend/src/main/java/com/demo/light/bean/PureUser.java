package com.demo.light.bean;

import com.demo.light.enums.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PureUser {
    private  Long id;
    private String username;
    private String password;
    private String provider;
    private Long providerId;
    private UserTypeEnum userType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status=1;
    private Integer accountNoExpire=1;
    private Integer credentialsNoExpire=1;
    private Integer accountNoLocked=1;
    private Integer accountEnabled=1;
    private LocalDateTime lastLogin;
}
