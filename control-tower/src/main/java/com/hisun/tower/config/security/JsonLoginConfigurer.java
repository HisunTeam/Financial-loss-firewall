package com.hisun.tower.config.security;

import com.hisun.tower.common.constant.SecurityConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JsonLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, JsonLoginConfigurer<H>, JsonUsernamePasswordAuthenticationFilter> {

    private final JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler;
    private final JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler;

    public JsonLoginConfigurer(JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler, JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler) {
        super(new JsonUsernamePasswordAuthenticationFilter(), SecurityConstants.LOGIN_URL);
        this.jsonAuthenticationSuccessHandler = jsonAuthenticationSuccessHandler;
        this.jsonAuthenticationFailureHandler = jsonAuthenticationFailureHandler;
    }

    @Override
    public void configure(H http) throws Exception {
        // 添加认证提供者
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        // 设置过滤器
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter = this.getAuthenticationFilter();
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler);

        // 将filter添加到过滤器链中 SecurityFilterChain
        http.addFilterBefore(jsonUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
