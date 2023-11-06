package com.hisun.tower.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hisun.tower.audit.AuditEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OracleSource extends AuditEntity<OracleSource> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3380293569032603065L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 端口
     */
    private int port;

    /**
     * 数据库名
     */
    @TableField("`database`")
    private String database;

    /**
     * 模式
     */
    @TableField("`schema`")
    private String schema;

    /**
     * 表名
     */
    @TableField("`table`")
    private String table;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
