package com.hisun.tower.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "测试连接对象", description = "测试连接对象")
public class TestConnection {

    @Schema(name = "dbms", description = "数据库系统")
    private String dbms;

    @Schema(name = "hostname", description = "主机名")
    private String hostname;

    @Schema(name = "port", description = "端口")
    private int port;

    @Schema(name = "table", description = "表名")
    private String table;

    @Schema(name = "isRac", description = "oracle是否为RAC")
    private boolean isRac;

    @Schema(name = "database", description = "数据库")
    private String database;

    @Schema(name = "username", description = "用户")
    private String username;

    @Schema(name = "password", description = "密码")
    private String password;
}
