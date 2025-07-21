import axios from 'axios';

/**
 * 提交课程反馈
 * @param data 包含课程ID和反馈内容的对象
 */
export function submitFeedback(data: { courseId: string | number; content: string }) {
  return axios.post('/api/feedback', data);
} 