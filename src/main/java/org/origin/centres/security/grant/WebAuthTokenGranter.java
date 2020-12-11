package org.origin.centres.security.grant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote 自定义 TokenGranter
 */
@SuppressWarnings("ALL")
public class WebAuthTokenGranter implements TokenGranter {

    protected CompositeTokenGranter delegate;
    protected AuthenticationManager manager;
    protected ClientDetailsService detailsService;
    protected AuthorizationServerTokenServices tokenServices;

    public WebAuthTokenGranter(AuthenticationManager manager, ClientDetailsService detailsService, AuthorizationServerTokenServices tokenServices) {
        this.manager = manager;
        this.detailsService = detailsService;
        this.tokenServices = tokenServices;
    }

    @Override
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (this.delegate == null) this.initDelegate();
        return this.delegate.grant(grantType, tokenRequest);
    }

    protected void initDelegate() {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(this.detailsService);  //使用默认
        if (this.detailsService != null && this.tokenServices != null) {
            AuthorizationCodeServices authorizationCodeServices = new InMemoryAuthorizationCodeServices();  //使用默认
            tokenGranters.add(new AuthorizationCodeTokenGranter(this.tokenServices, authorizationCodeServices, this.detailsService, requestFactory));
            tokenGranters.add(new RefreshTokenGranter(this.tokenServices, this.detailsService, requestFactory));
            tokenGranters.add(new ImplicitTokenGranter(this.tokenServices, this.detailsService, requestFactory));
            tokenGranters.add(new ClientCredentialsTokenGranter(this.tokenServices, this.detailsService, requestFactory));
            if (this.manager != null) {
                // ResourceOwnerPasswordTokenGranter
                tokenGranters.add(new PasswordOwnerGranter(this.manager, this.tokenServices, this.detailsService, requestFactory));
            }
            this.configure(tokenGranters, requestFactory);
        }
        this.delegate = new CompositeTokenGranter(tokenGranters);
    }

    protected void configure(List<TokenGranter> tokenGranters, OAuth2RequestFactory requestFactory) {

    }
}
