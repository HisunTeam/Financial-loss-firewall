package com.hisun.tower.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class Rule extends AuditEntity<Rule> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1345543661987872743L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 第一对比源类型
     */
    private String firstSourceType;

    /**
     * 第一对比源id
     */
    private String firstSource;

    /**
     * 主键，逗号分隔
     */
    private String firstKeys;

    /**
     * 数据列，逗号分割
     */
    private String firstColumns;

    /**
     * 过滤条件，逗号分隔
     */
    private String firstConditions;

    /**
     *
     */
    private LocalDateTime firstEventTime;

    /**
     * 第二对比源类型
     */
    private String secondSourceType;

    /**
     * 第二对比源id
     */
    private String secondSource;

    /**
     * 主键，逗号分隔
     */
    private String secondKeys;

    /**
     * 数据列，逗号分割
     */
    private String secondColumns;

    /**
     * 过滤条件，逗号分隔
     */
    private String secondConditions;

    /**
     *
     */
    private LocalDateTime secondEventTime;

    /**
     * 对比操作符
     */
    private String compareOperator;
}
