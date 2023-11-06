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
public class AlarmGroup extends AuditEntity<AlarmGroup> implements Serializable {
    @Serial
    private static final long serialVersionUID = -135636515956467372L;

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
     * alarm instance ids
     */
    private String alarmInstanceIds;

    /**
     * 备注
     */
    private String note;
    /**
     * 是否开启
     */
    private boolean enabled;
}
