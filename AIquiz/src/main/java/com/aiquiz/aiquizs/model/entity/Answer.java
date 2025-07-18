package com.aiquiz.aiquizs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("student_answer")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long studentId;
    @JsonSerialize(using = ToStringSerializer.class)
    private String choices;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;
    private Integer score;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}