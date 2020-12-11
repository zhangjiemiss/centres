package org.origin.centres.utils;

import org.origin.centres.security.entity.IUserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * @author 张杰
 * @version 2019-07-12
 * @apiNote 系统用户工具
 */
@SuppressWarnings({"ALL","unchecked"})
public class UserUtil {

    /**
     * 获取用户信息
     */
    public static UserDetails resUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    return (UserDetails) principal;
                }
            }
        }
        return null;
    }

    /**
     * 获取用户账号
     */
    public static String getAccount() {
        UserDetails user = resUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取用户ID
     */
    public static String getUserId() {
        Object Id = AliveUtil.getObjValue(resUser(), "id");
        if (Id != null) {
            return Id.toString();
        }
        return null;
    }

    /**
     * 获取登录用户是否是超级管理员
     */
    public static boolean isAdmin() {
        Boolean isAdmin = AliveUtil.invokeObj(resUser(), "isAdmin");
        return isAdmin != null ? isAdmin : false;
    }

    /**
     * 获取用户信息
     */
    public static IUserEntity getUser() {
        UserDetails user = resUser();
        if (user instanceof IUserEntity) {
            return (IUserEntity) user;
        }
        return null;
    }

    /**
     * 获取用户组织ID
     */
    public static String getOrgId() {
        Object OrgId = AliveUtil.getObjValue(resUser(), "orgId");
        if (OrgId != null) {
            return OrgId.toString();
        }
        return null;
    }

    /**
     * 获取用户信息
     */
    public static <T> T getUserVal(String name) {
        IUserEntity user = getUser();
        if (user != null) {
            Object value = user.get(name);
            if (value != null) {
                return (T) value;
            }
        }
        return null;
    }

    /**
     * 获取登录的client-id
     *
     * @return client-id
     */
    public String getClient() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        if (attributes == null) throw new IllegalArgumentException("请求为非客户端请求");
        HttpServletRequest request = attributes.getRequest();
        String authorization = request.getHeader("Authorization");
        if (authorization.startsWith("Basic ")) {
            String substring = authorization.substring(6);
            String decode = new String(Base64.getDecoder().decode(substring));
            if (decode.contains(":")) {
                int index = decode.indexOf(":");
                if (index > 0) {
                    return decode.substring(0, index);
                }
            }
        }
        return null;
    }

    /**
     * 获取 grant_type
     *
     * @return grant_type
     */
    public static String getGrantType() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        if (attributes == null) throw new IllegalArgumentException("请求为非客户端请求");
        HttpServletRequest request = attributes.getRequest();
        return request.getParameter("grant_type");
    }

    /**
     * 刷新用户信息及权限
     */
    public static void refresh(UserDetails entity) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        // 重新new一个token，因为Authentication中的权限是不可变的.
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                entity, authentication.getCredentials(), entity.getAuthorities());
        result.setDetails(authentication.getDetails());
        securityContext.setAuthentication(result);
    }
}
