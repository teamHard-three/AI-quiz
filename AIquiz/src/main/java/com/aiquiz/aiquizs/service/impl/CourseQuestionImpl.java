package com.aiquiz.aiquizs.service.impl;

import com.aiquiz.aiquizs.mapper.CourseMapper;
import com.aiquiz.aiquizs.mapper.CourseQuestionMapper;
import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.CourseQuestion;
import com.aiquiz.aiquizs.service.CourseQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CourseQuestionImpl extends ServiceImpl<CourseQuestionMapper, CourseQuestion> implements CourseQuestionService {

}