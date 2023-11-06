package com.hisun.tower.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name="登录对象", description="登录对象")
public class SysLoginModel {
    @Schema(name = "username", description = "账号")
    private String username;
    @Schema(name = "password", description = "密码")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
