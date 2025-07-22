import axios from 'axios';

/**
 * 学生提交课程反馈
 */
export function submitStudentFeedback(data: { courseId: string | number; studentId: string | number; content: string }) {
  return axios.post('/api/studentcourse/feedback', data);
}

/**
 * 教师查看课程反馈
 */
export function getCourseFeedback(courseId: string | number) {
  return axios.get(`/api/studentcourse/feedback/${courseId}`);
} 