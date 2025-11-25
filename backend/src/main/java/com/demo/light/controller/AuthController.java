package com.demo.light.controller;


import com.demo.light.bean.DTO.RegisterRequest;
import com.demo.light.bean.PureUser;
import com.demo.light.bean.UserProfile;
import com.demo.light.bean.UserStat;
import com.demo.light.enums.CodeEnum;
import com.demo.light.enums.UserTypeEnum;
import com.demo.light.result.R;
import com.demo.light.service.TokenBlacklistService;
import com.demo.light.service.UserService;
import com.demo.light.utils.JwtUtil;
import com.demo.light.utils.RedisUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


@RestController
public class AuthController {
    @Resource
    AuthenticationConfiguration authenticationConfiguration;

    @Resource
    JwtUtil jwtUtil;

    @Resource
    RedisUtil redisUtil;

    @Autowired
    UserService userService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;



    private static final String GITHUB_CLIENT_ID = "Ov23liIqURfB4Kqa115E";
    private static final String GITHUB_CLIENT_SECRET = "your_github_client_secret";
    private static final String GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_INFO_URL = "https://api.github.com/user";

    @PostMapping("/login")
    public R<Object> photographerLogin(@RequestParam("username") String username,@RequestParam("password") String password)throws Exception{


       try{
           PureUser user=userService.selectByUsername(username);
           List<String> authorities=userService.selectPermissionsByUsername(username);
           String userId=String.valueOf(user.getId());
           UserTypeEnum userType=user.getUserType();
           UsernamePasswordAuthenticationToken token=
                   new UsernamePasswordAuthenticationToken(username,password);
           Authentication authentication=authenticationConfiguration.getAuthenticationManager().authenticate(token);
           SecurityContextHolder.getContext().setAuthentication(authentication);

           String jwtToken=jwtUtil.createToken(userId,userType);

//           获取用户权限，并写入redis中
           String redisKey = "token-auth";
           redisUtil.set(redisKey,userId,authorities.toString());

           redisUtil.set(userId, jwtToken, 10000);

           return R.builder().code(200).data(jwtToken).msg("登录成功").build();
       }catch (BadCredentialsException | UsernameNotFoundException e) {
           return R.builder().code(400).msg("登录失败").data(e.getMessage()).build();
       } catch (Exception e) {
           return R.builder().code(400).msg("登录失败").data(e.getMessage()).build();
       }
    }


    @PostMapping("/github/callback")
    public R<String> githubCallback(@RequestParam("code") String code,@RequestParam("userType") String userType){
        if (code == null || code.isEmpty()) {
            return R.FAIL(CodeEnum.AUTHENTICATION_FAILED);
        }

        WebClient webClient= WebClient.builder().build();
        try{
            String accessToken=webClient.post()
                    .uri(GITHUB_ACCESS_TOKEN_URL)
                    .bodyValue(Map.of(
                            "client_id", GITHUB_CLIENT_ID,
                            "client_secret", GITHUB_CLIENT_SECRET,
                            "code", code
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block()
                    .get("access_token")
                    .toString();
            Map<String, Object> userInfo = webClient.get()
                    .uri(GITHUB_USER_INFO_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // 同步等待

            Long githubId = ((Number) userInfo.get("id")).longValue();
            String username = (String) userInfo.get("login");
            String avatar = (String) userInfo.get("avatar_url");


            PureUser user = userService.selectByProviderAndProviderId("github",githubId);

            if (user == null) {
                user = new PureUser();
                user.setProviderId(githubId);
                user.setUsername(username);
                user.setProvider("github");
                user.setUserType(UserTypeEnum.Customer); // 默认客户
                userService.insertOneUser(user);
            } else {
                userService.insertOneUser(user);
            }

            // 第四步：生成 JWT Token
            String token = jwtUtil.createToken(user.getUsername(), user.getUserType());

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", Map.of(
                    "token", token,
                    "userType", user.getUserType(),
                    "username", user.getUsername()
            ));
            return R.OK();
        }catch(Exception e){
            e.printStackTrace();
            return R.FAIL(CodeEnum.AUTHENTICATION_FAILED);

        }
    }
    //注册用户信息（无profile），同时初始化stat
    @PostMapping("/register")
    public R<Object> register(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("userType") String userType){

        Long id=UUID.randomUUID().hashCode()&Long.MAX_VALUE;

        PureUser user=PureUser.builder()
                .username(username)
                .password(new BCryptPasswordEncoder().encode(password))
                .userType(UserTypeEnum.fromValue(userType))
                .status(1)
                .accountNoExpire(1)
                .accountEnabled(1)
                .accountNoLocked(1)
                .credentialsNoExpire(1)
                .id(id)
                .createTime(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        int res = userService.insertOneUser(user);
        if (res==1){
            UserProfile userProfile=new UserProfile(id);
            userService.insertUserProfileAuto(userProfile);
            UserStat userStat =new UserStat(id);
            userService.initUserStat(userStat);
            return R.builder().code(200).msg("注册成功").build();
        }
        return R.FAIL(CodeEnum.INSERT_FAIL);
    }

//    @PostMapping("/register/photographer")
//    public R<?> register(@RequestPart("data")RegisterRequest request,
//                         BindingResult bindingResult){
//
//        if (bindingResult.hasErrors()){
//            return R.FAIL(CodeEnum.BAD_REQUEST);
//        }
//
//
//
//    }


    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        String token=jwtUtil.getTokenFromRequest(request);
        tokenBlacklistService.addToBlacklist(token, 1000);
        return R.OK();
    }

}
