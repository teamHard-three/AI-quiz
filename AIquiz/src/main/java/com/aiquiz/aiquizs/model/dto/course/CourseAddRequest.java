package com.aiquiz.aiquizs.model.dto.course;

import lombok.Data;

import java.io.Serializable;
@Data
public class CourseAddRequest implements Serializable {
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


    private static final long serialVersionUID = 1L;
}
