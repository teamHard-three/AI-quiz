package com.aiquiz.aiquizs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * 学生申请班级加入请求实体
 */
@Data
@TableName("class_join_request")
public class ClassJoinRequest implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long teacherId;

    private String status; // pending / approved / rejected

    private String reason; // 拒绝理由（可选）

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
