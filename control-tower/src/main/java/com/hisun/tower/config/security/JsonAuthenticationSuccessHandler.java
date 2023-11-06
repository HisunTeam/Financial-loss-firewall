package com.hisun.tower.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.tower.common.vo.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Instant;

@Slf4j
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;

    public JsonAuthenticationSuccessHandler(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        try {
            Instant now = Instant.now();
            long expiry = 36000L;
            // @formatter:off
            String[] roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
                    .toArray(new String[0]);
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(authentication.getName())
                    .claim("scope", roles)
                    .build();
            // @formatter:on
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.OK("登录成功", this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue())));
        } catch (Exception e) {
            // 组装对客户端响应信息
            log.error("生成用户口令异常", e);
            // 响应给客户端
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.error("登录失败,生成用户口令异常")));
        }
    }
}
