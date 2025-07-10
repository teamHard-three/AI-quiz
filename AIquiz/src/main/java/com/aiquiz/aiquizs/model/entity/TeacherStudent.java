package com.aiquiz.aiquizs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 老师与学生关系实体
 */
@TableName("teacher_student")
@Data
public class TeacherStudent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teacherId;

    private Long studentId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}