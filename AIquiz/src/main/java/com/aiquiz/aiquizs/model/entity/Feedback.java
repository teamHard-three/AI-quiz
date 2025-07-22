package com.aiquiz.aiquizs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生反馈实体类
 */
@Data
@TableName("feedback")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID，关联 course 表
     */
    private Long courseId;

    /**
     * 学生ID，关联 user 表
     */
    private Long studentId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 是否删除 0-未删 1-已删
     */
    @TableLogic
    private Integer isDelete;
}