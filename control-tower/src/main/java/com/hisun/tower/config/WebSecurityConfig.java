package com.hisun.tower.config;

import com.hisun.tower.common.constant.SecurityConstants;
import com.hisun.tower.config.security.*;
import com.hisun.tower.mapper.*;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {


    @Value("${jwt.public.key}")
    RSAPublicKey rsaPublicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey rsaPrivateKey;

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**")
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(new InMemoryUserDetailsManager(User.withUsername("actuator").password("{noop}actuator").build()));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain alarmSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/scene/sendEmail")
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .userDetailsService(new InMemoryUserDetailsManager(User.withUsername("alarm").password("{noop}alarm").build()));

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/", SecurityConstants.LOGIN_URL, "favicon.ico").permitAll()
                                .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable) // TODO 有时间要开启
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer
                                .jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(jsonBearerTokenAuthenticationEntryPoint())
                )
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                );

        http.apply(jsonLoginConfigurer(jsonAuthenticationSuccessHandler(jwtEncoder()), jsonAuthenticationFailureHandler()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper, SysRoleMapper sysRoleMapper,
                                                 SysRolePermissionMapper sysRolePermissionMapper, SysPermissionMapper sysPermissionMapper) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new CustomUserDetailsService(sysUserMapper, sysUserRoleMapper, sysRoleMapper, sysRolePermissionMapper, sysPermissionMapper);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler(JwtEncoder jwtEncoder) {
        return new JsonAuthenticationSuccessHandler(jwtEncoder);
    }

    @Bean
    JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler() {
        return new JsonAuthenticationFailureHandler();
    }

    @Bean
    JsonLoginConfigurer<HttpSecurity> jsonLoginConfigurer(JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler,
                                                          JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler) {
        return new JsonLoginConfigurer<>(jsonAuthenticationSuccessHandler, jsonAuthenticationFailureHandler);
    }

    @Bean
    JsonBearerTokenAuthenticationEntryPoint jsonBearerTokenAuthenticationEntryPoint(){
        return new JsonBearerTokenAuthenticationEntryPoint();
    }
}
