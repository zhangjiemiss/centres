package org.origin.centres.security.grant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote 自定义无密码授权 Provider
 */
public class NonPasswordAuthProvider extends AbstractAuthProvider {

    public NonPasswordAuthProvider(UserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        NonPasswordAuthToken result = new NonPasswordAuthToken(
                principal, authentication.getCredentials(),
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (NonPasswordAuthToken.class.isAssignableFrom(authentication));
    }
}
