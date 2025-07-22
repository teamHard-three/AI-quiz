
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
import com.aiquiz.aiquizs.model.entity.Feedback;
import com.aiquiz.aiquizs.service.FeedbackService;
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
    @Autowired
    private FeedbackService feedbackService;

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
    
    /**
     * 学生提交反馈
     * @param feedback 包含 courseId, studentId, content 的反馈实体
     * @return 提交结果
     */
    @PostMapping("/feedback")
    public BaseResponse<String> submitFeedback(@RequestBody Feedback feedback) {
        // 1. 权限校验：检查当前登录用户是否为学生
        if (!Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.DEFAULT_ROLE)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "只有学生才能提交反馈");
        }

        // 2. 参数校验：检查核心参数是否为空
        if (feedback == null || feedback.getCourseId() == null || feedback.getStudentId() == null ||
            feedback.getContent() == null || feedback.getContent().trim().isEmpty()) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误，反馈内容不能为空");
        }

        // 3. 安全校验：防止学生替其他同学提交反馈
        if (!feedback.getStudentId().equals(UserHolder.getUser().getId())) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "不能为他人提交反馈");
        }

        try {
            // 4. 调用服务层并处理结果
            boolean result = feedbackService.save(feedback);
            if (result) {
                return ResultUtils.success("反馈提交成功");
            } else {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR, "反馈提交失败，请稍后再试");
            }
        } catch (Exception e) {
            // 5. 异常处理：捕获未知异常，返回系统错误
            log.error("提交反馈时发生系统异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误，请稍后再试");
        }
    }

    /**
     * 教师查看某课程的所有反馈
     * @param courseId 课程ID
     * @return 该课程的反馈列表
     */
    @GetMapping("/feedback/{courseId}")
    public BaseResponse<List<Feedback>> getFeedbackByCourse(@PathVariable Long courseId) {
        // 1. 权限校验：检查当前登录用户是否为教师
        // 假设教师角色在数据库中存的是 "teacher"
        if (!"teacher".equals(UserHolder.getUser().getUserRole())) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "只有教师才能查看反馈");
        }

        // 2. 参数校验：检查课程ID是否合法
        if (courseId == null || courseId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "课程ID无效");
        }

        try {
            // 3. 业务校验：检查该教师是否为这门课的授课老师
            Course course = courseService.getById(courseId);
            if (course == null) {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
            }
            if (!course.getTeacherId().equals(UserHolder.getUser().getId())) {
                return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "您不是该课程的老师，无权查看");
            }

            // 4. 调用服务层获取数据
            List<Feedback> feedbackList = feedbackService.lambdaQuery()
                    .eq(Feedback::getCourseId, courseId)
                    .eq(Feedback::getIsDelete, 0)
                    .orderByDesc(Feedback::getCreateTime) // 按时间倒序排列，最新的在最前面
                    .list();
            return ResultUtils.success(feedbackList);
        } catch (Exception e) {
            // 5. 异常处理：捕获未知异常，返回系统错误
            log.error("获取课程反馈列表时发生系统异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取反馈列表失败，请稍后再试");
        }
    }
}