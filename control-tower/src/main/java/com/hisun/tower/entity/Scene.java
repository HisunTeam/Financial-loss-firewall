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
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Scene extends AuditEntity<Scene> implements Serializable {
    @Serial
    private static final long serialVersionUID = -8168612351447275726L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 规则id
     */
    private String ruleName;
    /**
     * 监控组名称
     */
    private String alarmGroupName;
    /**
     * 发布状态
     */
    private String publishStatus;
    /**
     * 发布任务ID
     */
    private String jobId;
}
