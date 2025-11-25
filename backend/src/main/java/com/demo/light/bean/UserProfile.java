package com.demo.light.bean;

import com.demo.light.enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    private Long id;
    private Long userId;
    private String avatar;
    private String nickname;
    private GenderEnum gender;
    private LocalDate birthday;
    private String bio;
    public UserProfile(Long userId){
        this.userId=userId;
    }
}
