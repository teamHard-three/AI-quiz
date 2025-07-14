package com.aiquiz.aiquizs.controller;

import com.aiquiz.aiquizs.commom.BaseResponse;
import com.aiquiz.aiquizs.commom.ErrorCode;
import com.aiquiz.aiquizs.commom.ResultUtils;
import com.aiquiz.aiquizs.model.UserConstant;
import com.aiquiz.aiquizs.model.dto.course.CourseAddRequest;
import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.StudentCourse;
import com.aiquiz.aiquizs.service.CourseService;
import com.aiquiz.aiquizs.service.StudentCourseService;
import com.aiquiz.aiquizs.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admincourse")
@Slf4j
public class courseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentCourseService studentCourseService;
    @PostMapping("/addCourse")
    public BaseResponse<String> addCourse(@RequestBody CourseAddRequest courseAddRequest) {
        // 参数校验
        if(UserHolder.getUser().getUserRole()!= UserConstant.ADMIN_ROLE)
        {
          return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限添加课程");
        };
        if (courseAddRequest== null) {
            return ResultUtils.error(400, "请求体不能为空");
        }
        boolean result = courseService.addCourse(courseAddRequest);
        if (result) {
            return ResultUtils.success("课程添加成功");
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "课程添加失败，请稍后再试");
        }
    }
    @GetMapping("/getCourseList")
    public BaseResponse<List<Course>> getCourseList() {
        try {
            return ResultUtils.success(courseService.getCourseList());
        } catch (Exception e) {
            log.error("获取课程列表失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取课程列表失败，请稍后再试");
        }
    }
    @GetMapping("/getCourseById/{id}")//查询回显
    public BaseResponse<Course> getCourseById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID无效");
        }
        Course course = courseService.getById(id);
        if (course == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        }
        return ResultUtils.success(course);
    }
    @PostMapping("/updateCourse/{id}")
    public BaseResponse<String> updateCourse(@PathVariable Long id, @RequestBody CourseAddRequest courseAddRequest) {
        // 参数校验
        if(UserHolder.getUser().getUserRole()!= UserConstant.ADMIN_ROLE)
        {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限修改课程");
        }
        if (id == null || id <= 0 || courseAddRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID或请求体无效");
        }
        boolean result = courseService.updateCourse(id, courseAddRequest);
        if (result) {
            return ResultUtils.success("课程更新成功");
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "课程更新失败，请稍后再试");
        }
    }
    @GetMapping("/deleteCourse")
    public BaseResponse<String> deleteCourse(@RequestParam List<Long> ids) {
        // 参数校验
        if(UserHolder.getUser().getUserRole()!= UserConstant.ADMIN_ROLE)
        {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限删除课程");
        }
        if (ids == null || ids.isEmpty()) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID列表不能为空");
        }
        boolean result = courseService.removeByIds(ids);
        if (result) {
            return ResultUtils.success("课程删除成功");
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "课程删除失败，请稍后再试");
        }
    }
    @GetMapping("/joinCourserequest/{id}")
    public BaseResponse<List<StudentCourse>> joinCourse(@PathVariable Long id) {
        if (UserHolder.getUser().getUserRole() != UserConstant.ADMIN_ROLE) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限处理请求");
        }
        if (id == null || id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID无效");
        }
        List<StudentCourse> result = studentCourseService.getallrequest(id);
        if (result != null && !result.isEmpty()) {
            return ResultUtils.success(result);
        } else {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "没有找到相关请求");
        }
    }
    @GetMapping("/acceptJoinRequest/{id}")
    public BaseResponse<String> acceptJoinRequest(@PathVariable Long id,@RequestParam("status") String status) {
        if (UserHolder.getUser().getUserRole() != UserConstant.ADMIN_ROLE) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限处理请求");
        }
        if (id == null || id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求ID无效");
        }
        boolean result = studentCourseService.acceptJoinRequest(id,status);
        if (result) {
            return ResultUtils.success("请求已接受");
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "处理请求失败，请稍后再试");
        }
    }
}
