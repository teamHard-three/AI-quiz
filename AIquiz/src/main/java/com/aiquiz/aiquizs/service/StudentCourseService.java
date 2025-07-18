package com.aiquiz.aiquizs.service;

import com.aiquiz.aiquizs.model.dto.answer.AnswerADDRequest;
import com.aiquiz.aiquizs.model.entity.StudentCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface StudentCourseService extends IService<StudentCourse> {

    boolean joinCourse(Long id);

    List<StudentCourse> getallrequest(Long id);

    boolean acceptJoinRequest(Long id, String status);

    boolean quitCourse(Long id);

    int answerQuestion(AnswerADDRequest answer) throws JsonProcessingException;
}
