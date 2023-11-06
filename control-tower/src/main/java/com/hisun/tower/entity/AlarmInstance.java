package com.hisun.tower.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class AlarmInstance extends AuditEntity<AlarmInstance> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7106173005250542081L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 配置
     */
    private String params;
    /**
     * 是否开启
     */
    private boolean enabled;
}
