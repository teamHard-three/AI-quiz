package com.aiquiz.aiquizs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("course")
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 授课教师用户ID，关联user表id
     */
    private Long teacherId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除标记，0-未删，1-已删
     */
    @TableLogic
    private Integer isDelete;
}
