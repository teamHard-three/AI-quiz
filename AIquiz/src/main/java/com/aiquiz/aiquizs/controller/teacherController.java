package com.aiquiz.aiquizs.controller;

import com.aiquiz.aiquizs.commom.BaseResponse;
import com.aiquiz.aiquizs.commom.ErrorCode;
import com.aiquiz.aiquizs.commom.ResultUtils;
import com.aiquiz.aiquizs.model.UserConstant;
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
   public BaseResponse<String> upload(  @RequestParam("file") MultipartFile file,
                                           @RequestParam("courseId") Long courseId) throws IOException {

         if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
             return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限上传课程内容");
         }
            if (file.isEmpty() || courseId == null || courseId <= 0) {
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "文件或课程ID无效");
            }
            boolean result = teacherService.uploadCourseContent(file, courseId);

            return result ? ResultUtils.success("课程内容上传成功") :
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

}



