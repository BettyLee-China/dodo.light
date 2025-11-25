package com.demo.light.service.Impl;

import com.demo.light.bean.PureUser;
import com.demo.light.bean.User;
import com.demo.light.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceDetails implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        PureUser user=userMapper.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("用户不存在");
        }

        // 查询权限字符串
        List<String> permissionCodes = userMapper.findPermissionsByUsername(username);
        for(String permissionCode : permissionCodes){
            System.out.println(permissionCode);
        }
        // 手动构建 authorities
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加角色
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserType().getValue()));

        // 添加具体权限
        if (permissionCodes != null) {
            permissionCodes.forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p))
            );
        }
        for(SimpleGrantedAuthority authority : authorities ){
            System.out.println(authority);
        }

        return new User(user,authorities);
    }
}
