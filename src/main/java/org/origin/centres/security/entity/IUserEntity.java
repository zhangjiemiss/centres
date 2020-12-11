package org.origin.centres.security.entity;

import org.origin.centres.constants.IConstant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author zhangjie
 * @version 2018-04-26 15:24:01
 * @apiNote 系统用户Entity
 */
@SuppressWarnings({"ALL","unchecked"})
public class IUserEntity extends HashMap<String, Object> implements UserDetails {

    @Override
    public String getUsername() {
        Object username = get("username");
        return username != null ? username.toString() : null;
    }

    @Override
    public String getPassword() {
        Object password = get("password");
        return password != null ? password.toString() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Object Authorities = get("authorities");
        return Authorities != null ? (Collection<? extends GrantedAuthority>) Authorities : new ArrayList<SimpleGrantedAuthority>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getStatus() != null && this.getStatus().equals(IConstant.Usable.toString());
    }

    public String getId() {
        Object id = get("id");
        return id != null ? id.toString() : null;
    }

    public String getStatus() {
        Object status = get("status");
        return status != null ? status.toString() : null;
    }

    public boolean isAdmin() {
        Object IsAdmin = get("isAdmin");
        return IsAdmin != null && String.valueOf(IsAdmin).equals("true");
    }

    public String getOrgId() {
        Object orgId = get("orgId");
        return orgId != null ? orgId.toString() : null;
    }

    public void setId(String id) {
        this.put("id", id);
    }

    public void setUsername(String username) {
        this.put("username", username);
    }

    public void setPassword(String password) {
        this.put("password", password);
    }

    public void setStatus(Integer status) {
        this.put("status", status);
    }

    public void setAdmin(boolean isAdmin) {
        this.put("isAdmin", isAdmin);
    }

    public void setOrgId(String orgId) {
        this.put("orgId", orgId);
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.put("authorities", authorities);
    }

}

