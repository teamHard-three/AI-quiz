package com.aiquiz.aiquizs.mapper;

import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.StudentCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface studentcourseMapper extends BaseMapper<StudentCourse> {
}
