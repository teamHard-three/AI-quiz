package com.aiquiz.aiquizs.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("student_course")
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选课记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生用户ID（user.id 且 userRole = 'student'）
     */
    private Long studentId;

    /**
     * 课程ID（course.id）
     */
    private Long courseId;

    /**
     * 选课状态：pending=待审批，approved=已通过，rejected=已驳回
     */
    private String status;

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
     * 是否删除（逻辑删除字段：0-未删，1-已删）
     */
    @TableLogic
    private Integer isDelete;
}
