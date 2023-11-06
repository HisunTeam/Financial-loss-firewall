package com.hisun.tower.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Configuration(proxyBeanMethods = false)
@MapperScan("com.hisun.tower.mapper")
public class MybatisPlusConfig {

    @Component
    public class MybatisAuditHandler implements MetaObjectHandler {
        @Override
        public void insertFill(MetaObject metaObject) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            this.strictInsertFill(metaObject, "createBy", String.class, userId);
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            this.strictUpdateFill(metaObject, "updateBy", String.class, userId);
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
    }
}
