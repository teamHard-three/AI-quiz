package com.aiquiz.aiquizs.service.impl;

import com.aiquiz.aiquizs.mapper.CourseMapper;
import com.aiquiz.aiquizs.mapper.studentcourseMapper;
import com.aiquiz.aiquizs.model.dto.answer.AnswerADDRequest;
import com.aiquiz.aiquizs.model.entity.*;
import com.aiquiz.aiquizs.service.AnswerService;
import com.aiquiz.aiquizs.service.CourseQuestionService;
import com.aiquiz.aiquizs.service.StudentCourseService;
import com.aiquiz.aiquizs.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class studentCourseServiceImpl extends ServiceImpl<studentcourseMapper, StudentCourse> implements StudentCourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseQuestionService courseQuestionService;
    @Autowired
    private AnswerService answerService;
    @Override
    public int answerQuestion(AnswerADDRequest answer) throws JsonProcessingException {
        //查询学生是否在课程中
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("studentid", answer.getStudentId())
                .eq("courseid", answer.getCourserId())
                .eq("status", "approved"); // 确保学生在课程中且状态为active
        long count = this.baseMapper.selectCount(wrapper);
        if (count == 0) {
            log.error("学生未加入课程或状态不正确，无法回答问题，课程ID: {}", answer.getCourserId());
            return -1; // 学生未加入课程或状态不正确，返回false
        }
        // 如果学生在课程中，执行回答问题的逻辑
        //查询课程题目是否存在
        QueryWrapper<CourseQuestion> questionWrapper = new QueryWrapper<>();
        wrapper.eq("id", answer.getQuestionId());
        CourseQuestion courseQuestion = courseQuestionService.getOne(questionWrapper);
        if (courseQuestion == null) {
            log.error("课程题目不存在，ID: {}", answer.getQuestionId());
            return -1; // 课程题目不存在，返回false
        }
        JSONArray jsonArray = new JSONArray(courseQuestion.getQuestion());

        // 使用 Jackson 把 jsonArray.toString() 转成 List<QuestionContentDTO>
        ObjectMapper mapper = new ObjectMapper();
        List<QuestionContentDTO> list = mapper.readValue(
                jsonArray.toString(),
                new TypeReference<List<QuestionContentDTO>>() {}
        );
        // 检查答案是否正确
        AtomicInteger score= new AtomicInteger();
        if(list.size()==answer.getChoices().size())
        {
            for (int i=0;i<list.size();i++)
            {
                int finalI = i;
                list.get(i).getOptions().forEach(option -> {
                    if (Objects.equals(option.getKey(), answer.getChoices().get(finalI)) && option.getScore()==1) {
                        score.getAndIncrement();
                    }
                });
            }
        }
        //将答案存入数据库
        Answer answerEntity = new Answer();
        answerEntity.setStudentId(answer.getStudentId());
        answerEntity.setCourseId(answer.getCourserId());
        answerEntity.setQuestionId(answer.getQuestionId());
        answerEntity.setChoices(answer.getChoices().toString());
        answerEntity.setScore(score.get());
         answerService.save(answerEntity);


        return score.get();


    }

    @Override
    public boolean quitCourse(Long id) {
        // 查询学生课程记录
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("studentid", UserHolder.getUser().getId())
                .eq("courseid", id);

        // 获取学生课程记录
        StudentCourse studentCourse = this.baseMapper.selectOne(wrapper);
        if (studentCourse == null) {
            log.error("学生课程记录不存在，id: {}", id);
            return false; // 学生课程记录不存在，返回false
        }

        // 删除学生课程记录
        boolean result = this.removeById(studentCourse.getId());
        if (result) {
            log.info("成功退出课程，id: {}", id);
            return true; // 成功退出课程，返回true
        } else {
            log.error("退出课程失败，id: {}", id);
            return false; // 退出课程失败，返回false
        }
    }

    @Override
    public boolean acceptJoinRequest(Long id, String status) {

        // 查询学生课程记录
        StudentCourse studentCourse = this.baseMapper.selectById(id);
        if (studentCourse == null) {
            log.error("学生课程记录不存在，id: {}", id);
            return false; // 学生课程记录不存在，返回false
        }

        // 更新状态
        studentCourse.setStatus(status);
        boolean result = this.updateById(studentCourse);
        if (result) {
            log.info("成功更新学生课程状态，id: {}, status: {}", id, status);
            return true; // 成功更新状态，返回true
        } else {
            log.error("更新学生课程状态失败，id: {}", id);
            return false; // 更新状态失败，返回false
        }
    }

    @Override
    public List<StudentCourse> getallrequest(Long id) {
        // 查询所有加入该课程的学生
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("courseid", id);
        List<StudentCourse> studentCourses = this.baseMapper.selectList(wrapper);

        if (studentCourses.isEmpty()) {
            log.info("没有学生加入课程，id: {}", id);
            return null; // 没有学生加入课程，返回null或空列表
        } else {
            log.info("查询到{}名学生加入课程，id: {}", studentCourses.size(), id);
            return studentCourses; // 返回所有加入该课程的学生列表
        }
    }

    @Override
    public boolean joinCourse(Long id) {
        //查询课程是否存在
        Course studentCourse = courseMapper.selectById(id);
        if (studentCourse == null) {
            log.error("课程不存在，id: {}", id);
            return false; // 课程不存在，返回false
        }
        // 如果课程存在，判断是否加入课程
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("studentid", UserHolder.getUser().getId())
                .eq("courseid", id);

        long count = this.baseMapper.selectCount(wrapper);
    if(count>0)
    {
        log.info("用户已加入课程，id: {}", id);
        return false;
    } else {
        // 用户未加入课程，执行加入操作
        StudentCourse newStudentCourse = new StudentCourse();
        newStudentCourse.setStudentId(UserHolder.getUser().getId());
        newStudentCourse.setCourseId(id);
        newStudentCourse.setStatus("pending"); // 设置状态为active或其他适当的值
        boolean result = this.save(newStudentCourse);
        if (result) {
            log.info("用户成功加入课程，id: {}", id);
            return true; // 成功加入课程，返回true
        } else {
            log.error("用户加入课程失败，id: {}", id);
            return false; // 加入课程失败，返回false
        }
    }

    }
}
