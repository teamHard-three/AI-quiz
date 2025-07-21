package com.aiquiz.aiquizs.controller;

import com.aiquiz.aiquizs.commom.BaseResponse;
import com.aiquiz.aiquizs.commom.ErrorCode;
import com.aiquiz.aiquizs.commom.ResultUtils;
import com.aiquiz.aiquizs.model.UserConstant;
import com.aiquiz.aiquizs.model.dto.answer.Answerexit;
import com.aiquiz.aiquizs.model.dto.answer.message;
import com.aiquiz.aiquizs.model.entity.CourseQuestion;
import com.aiquiz.aiquizs.model.vo.CourseVO;
import com.aiquiz.aiquizs.service.CourseService;
import com.aiquiz.aiquizs.service.StudentCourseService;
import com.aiquiz.aiquizs.service.TeacherService;
import com.aiquiz.aiquizs.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
@RestController
@RequestMapping("/teachercourse")
@Slf4j
public class teacherController {
    @Autowired
    private TeacherService teacherService;

    @PostMapping("/upload")
    public BaseResponse<Integer> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam("courseId") Long courseId) throws IOException {

        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限上传课程内容");
        }
        if (file.isEmpty() || courseId == null || courseId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "文件或课程ID无效");
        }
        int result = teacherService.uploadCourseContent(file, courseId);

        return result > 0 ? ResultUtils.success(result) :
                ResultUtils.error(ErrorCode.OPERATION_ERROR, "课程内容上传失败，请稍后再试");
    }

    @GetMapping("/getCourseList/{id}") //获取教师的课程列表
    public BaseResponse<List<CourseVO>> getCourseList(@PathVariable Long id) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限查看课程列表");
        }
        try {
            List<CourseVO> courseList = teacherService.getCourseList(id);
            return ResultUtils.success(courseList);
        } catch (Exception e) {
            log.error("获取课程列表失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取课程列表失败，请稍后再试");
        }
    }

    @GetMapping("/createQuestion/{page}")
    public BaseResponse<CourseQuestion> createQuestion(@PathVariable String page, @RequestParam("courseid") Long courseid) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {

        return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限创建题目");
             }
            try {
                CourseQuestion courseQuestion = teacherService.createQuestion(page, courseid);
                return ResultUtils.success(courseQuestion);
            } catch (Exception e) {
                log.error("生成题目失败", e);
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取课程列表失败，请稍后再试");
            }

        }
        @GetMapping("/getCourseContent/{courseId}")
        public BaseResponse<Answerexit> getCourseContent(@PathVariable Long courseId) {
            if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
                return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限查看课程内容");
            }
            try {
                Answerexit answerexit = teacherService.isAnswerexit(courseId);
                return ResultUtils.success(answerexit);
            } catch (Exception e) {
                log.error("获取课程内容失败", e);
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取课程内容失败，请稍后再试");
            }
        }
//        @GetMapping("/getQuestion/{courseId}")
//        public BaseResponse<CourseQuestion> getQuestion(@PathVariable Long courseId) {
////            if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
////                return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限查看题目");
////            }
//            try {
//                CourseQuestion questions = teacherService.getQuestion(courseId);
//                return ResultUtils.success(questions);
//            } catch (Exception e) {
//                log.error("获取题目失败", e);
//                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取题目失败，请稍后再试");
//            }
//        }
    @PostMapping("/getQuestion/{courseId}")
    public BaseResponse<CourseQuestion> getQuestion(@RequestBody message me,@PathVariable Long courseId) {
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限查看题目");
        }
        try {
            CourseQuestion questions = teacherService.getQuestion(me,courseId);
            return ResultUtils.success(questions);
        } catch (Exception e) {
            log.error("获取题目失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取题目失败，请稍后再试");
        }
    }

}




