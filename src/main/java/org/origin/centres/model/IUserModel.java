package org.origin.centres.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote 用户数据模型
 */
public interface IUserModel extends UserDetails {

    @Override
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    default String getPassword() {
        return null;
    }

    @Override
    default String getUsername() {
        return null;
    }

    @Override
    default boolean isAccountNonExpired() {
        return true;
    }

    @Override
    default boolean isAccountNonLocked() {
        return true;
    }

    @Override
    default boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    default boolean isEnabled() {
        return true;
    }

    default boolean isAdmin() {
        return true;
    }
}
