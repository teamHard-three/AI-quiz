package com.aiquiz.aiquizs.service.impl;

import com.aiquiz.aiquizs.commom.ErrorCode;
import com.aiquiz.aiquizs.exception.BusinessException;
import com.aiquiz.aiquizs.mapper.CourseMapper;
import com.aiquiz.aiquizs.mapper.UserMapper;
import com.aiquiz.aiquizs.model.UserConstant;
import com.aiquiz.aiquizs.model.dto.course.CourseAddRequest;
import com.aiquiz.aiquizs.model.entity.Course;
import com.aiquiz.aiquizs.model.entity.User;
import com.aiquiz.aiquizs.model.vo.CourseVO;
import com.aiquiz.aiquizs.service.CourseService;
import com.aiquiz.aiquizs.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<CourseVO> getstudentCourseList(Long id) {
        // 获取当前用户角色
        if (Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.DEFAULT_ROLE)) {

            List<CourseVO> courseList = courseMapper.selectAllWithSelected(id);
            // 如果课程列表为空，返回空列表
            if (courseList == null || courseList.isEmpty()) {
                return List.of(); // 返回一个空列表而不是null
            }
            // 返回课程列表
            return courseList;
        } else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看课程");
        }
    }

    @Override
    public List<Course> getCourseList() {
        // 获取当前用户角色
        if (Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.ADMIN_ROLE)) {
            // 查询所有课程
            List<Course> courseList = this.baseMapper.selectList(null);
            // 如果课程列表为空，返回空列表
            if (courseList == null || courseList.isEmpty()) {
                return List.of(); // 返回一个空列表而不是null
            }
            // 返回课程列表
            return courseList;
        } else if (Objects.equals(UserHolder.getUser().getUserRole(), UserConstant.TEACHER_ROLE)) {
            // 查询教师的课程
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacherId", UserHolder.getUser().getId());
            List<Course> courseList = this.baseMapper.selectList(queryWrapper);
            // 如果课程列表为空，返回空列表
            if (courseList == null || courseList.isEmpty()) {
                return List.of(); // 返回一个空列表而不是null
            }
            // 返回课程列表
            return courseList;
        } else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看课程");

        }
    }

    @Override
    public boolean updateCourse(Long id, CourseAddRequest courseAddRequest) {
        // 参数校验
        if (id == null || courseAddRequest == null || courseAddRequest.getName() == null || courseAddRequest.getDescription() == null || courseAddRequest.getTeacherId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查教师是否存在
        User teacher = userMapper.selectById(courseAddRequest.getTeacherId());
        if (teacher == null || !"teacher".equals(teacher.getUserRole())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教师不存在或不是教师角色");
        }
        // 创建课程实体
        Course course = new Course();
        course.setId(id);
        course.setName(courseAddRequest.getName());
        course.setDescription(courseAddRequest.getDescription());
        course.setTeacherId(courseAddRequest.getTeacherId());

        // 更新课程信息
        boolean updateResult = this.updateById(course);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败，数据库错误");
        }
        return true;
    }

    @Override
    public boolean addCourse(CourseAddRequest courseAddRequest) {
        // 参数校验
        if (courseAddRequest.getName() == null || courseAddRequest.getDescription() == null || courseAddRequest.getTeacherId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        // 检查教师是否存在
        User teacher = userMapper.selectById(courseAddRequest.getTeacherId());
        if (teacher == null || !"teacher".equals(teacher.getUserRole())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教师不存在或不是教师角色");
        }
        // 创建课程实体

        synchronized (courseAddRequest.getName().intern()) {

            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", courseAddRequest.getName());
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程名重复");
            }
            // 3. 插入数据
            Course course = new Course();
            course.setName(courseAddRequest.getName());
            course.setDescription(courseAddRequest.getDescription());
            course.setTeacherId(courseAddRequest.getTeacherId());
            boolean saveResult = this.save(course);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建失败，数据库错误");
            }
            return true;
        }

    }
}