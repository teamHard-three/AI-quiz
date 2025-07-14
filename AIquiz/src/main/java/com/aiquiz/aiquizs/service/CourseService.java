package com.aiquiz.aiquizs.service;

import com.aiquiz.aiquizs.model.dto.course.CourseAddRequest;
import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.vo.CourseVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface CourseService extends IService<Course> {

    boolean addCourse(CourseAddRequest courseAddRequest);

    List<Course> getCourseList();

    boolean updateCourse(Long id, CourseAddRequest courseAddRequest);

    List<CourseVO> getstudentCourseList(Long id);


}
