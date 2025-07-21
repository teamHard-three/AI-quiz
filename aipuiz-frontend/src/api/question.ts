import axios from 'axios';

// 1. 创建课程题目（分页+课程ID）
export function createQuestion(page: number, courseId: number | string) {
  return axios.get(`/api/teachercourse/createQuestion/${page}`, {
    params: { courseid: courseId }
  });
}

// 2. 根据课程ID获取题目列表
export function getQuestionList(courseId: number | string) {
  return axios.get(`/api/teachercourse/getQuestion/${courseId}`);
}

// 3. 检查课程是否存在题目
export function checkCourseContent(courseId: number | string) {
  return axios.get(`/api/teachercourse/getCourseContent/${courseId}`);
}

// 4. 查看课程题目（学生/普通用户）
export function showQuestion(courseId: number | string) {
  return axios.get(`/api/studentcourse/showQuestion/${courseId}`);
}

// 5. 学生回答问题
export function answerQuestion(courseId: number | string, data: {
  courserId: number | string;
  studentId: number | string;
  questionId: number | string;
  choices: string[];
}) {
  return axios.post(`/api/studentcourse/answer/${courseId}`, data);
}