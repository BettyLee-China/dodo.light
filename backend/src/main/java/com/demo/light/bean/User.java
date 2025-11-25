package com.demo.light.bean;

import com.demo.light.enums.UserTypeEnum;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;



@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User   implements UserDetails, Serializable {
     private PureUser user;
     private List<String> permissions;
     private  Collection<? extends GrantedAuthority> authorities;

    public User(PureUser user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // 添加角色
        if (user.getUserType() != null) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()));
        }

        // 添加具体权限
        if (permissions != null) {
            permissions.forEach(p ->
                    authorityList.add(new SimpleGrantedAuthority(p))
            );
        }

        this.authorities = Collections.unmodifiableList(authorityList);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
