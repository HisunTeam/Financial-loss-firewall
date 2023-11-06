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
public class SourceType extends AuditEntity<SourceType> implements Serializable {
    @Serial
    private static final long serialVersionUID = 8438202643072926801L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 数据源类型
     */
    private String sourceType;

    /**
     * 数据源名称
     */
    private String sourceName;

    /**
     * 数据源详情存放表名
     */
    private String sourceTable;
}
