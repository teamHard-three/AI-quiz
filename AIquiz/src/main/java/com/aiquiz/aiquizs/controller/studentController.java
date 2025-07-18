
package com.aiquiz.aiquizs.controller;

import com.aiquiz.aiquizs.commom.BaseResponse;
import com.aiquiz.aiquizs.commom.ErrorCode;
import com.aiquiz.aiquizs.commom.ResultUtils;
import com.aiquiz.aiquizs.model.UserConstant;
import com.aiquiz.aiquizs.model.dto.answer.AnswerADDRequest;
import com.aiquiz.aiquizs.model.dto.course.CourseAddRequest;
import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.CourseQuestion;
import com.aiquiz.aiquizs.model.vo.CourseVO;
import com.aiquiz.aiquizs.service.CourseService;
import com.aiquiz.aiquizs.service.StudentCourseService;
import com.aiquiz.aiquizs.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/studentcourse")
@Slf4j
public class studentController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentCourseService studentCourseService;

    @GetMapping("/getCourseList/{id}")  //获取学生加入的课程列表
    public BaseResponse<List<CourseVO>> getCourseList(@PathVariable Long id) {
      System.out.println("获取学生加入的课程列表，学生ID：" + UserHolder.getUser().getUserRole());

        try {
            return ResultUtils.success(courseService.getstudentCourseList(id));
        } catch (Exception e) {
            log.error("获取课程列表失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取课程列表失败，请稍后再试");
        }
    }
    @GetMapping("/joinCourse/{id}") //学生加入请求
    public BaseResponse<String> joinCourse(@PathVariable Long id) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.DEFAULT_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限加入课程");
        }

        if (id == null || id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID无效");
        }
        boolean result = studentCourseService.joinCourse(id);
        if (result) {
            return ResultUtils.success("成功加入课程");
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "加入课程失败，请稍后再试");
        }
    }
    @GetMapping("/quitCourse/{id}") //查询回显
    public BaseResponse<String> quitCourse(@PathVariable Long id) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.DEFAULT_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限退出课程");
        }

        if (id == null || id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID无效");
        }
        boolean result = studentCourseService.quitCourse(id);
        if (result) {
            return ResultUtils.success("成功退出课程");
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "退出课程失败，请稍后再试");
        }
    }
    @GetMapping("/showQuestion/{courseid}") //查看问题
    public BaseResponse<CourseQuestion> showQuestion(@PathVariable Long courseid) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.DEFAULT_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限查看问题");
        }

        if (courseid == null || courseid <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID无效");
        }
        try {
            CourseQuestion courseQuestion = courseService.getQuestion(courseid);
            return ResultUtils.success(courseQuestion);
        } catch (Exception e) {
            log.error("获取问题失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取问题失败，请稍后再试");
        }
    }
    @PostMapping("/answer/{courseid}") //学生回答问题
    public BaseResponse<String> answerQuestion(@RequestBody AnswerADDRequest answer) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.DEFAULT_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限回答问题");
        }
        if (answer == null || answer.getCourserId() == null || answer.getStudentId() == null || answer.getChoices() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        try {
            int result = studentCourseService.answerQuestion(answer);
            if (result!=-1) {
                return ResultUtils.success("回答成功,你的得分是"+ result + "分(总分10分)");
            } else {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR, "回答失败，请稍后再试");
            }
        } catch (Exception e) {
            log.error("回答问题失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "回答问题失败，请稍后再试");
        }


    }
}