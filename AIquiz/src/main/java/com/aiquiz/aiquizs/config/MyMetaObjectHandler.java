package com.aiquiz.aiquizs.config;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
/**
 * MyBatis-Plus 自动填充配置类
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        // 判断字段是否为空，再填充
        if (getFieldValByName("createTime", metaObject) == null) {
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        }
        if (getFieldValByName("updateTime", metaObject) == null) {
            this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        }
    }

    // 更新时自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
