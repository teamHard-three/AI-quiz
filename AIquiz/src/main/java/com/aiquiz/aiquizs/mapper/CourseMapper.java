package com.aiquiz.aiquizs.mapper;

import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.User;
import com.aiquiz.aiquizs.model.vo.CourseVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    List<CourseVO> selectAllWithSelected(@Param("studentId") Long studentId);
}