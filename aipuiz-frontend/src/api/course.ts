import axios from 'axios';

// ===================== 管理员接口 =====================

// 添加课程
export function addCourse(data: { name: string; description: string; teacherId: string }) {
  return axios.post('/api/admincourse/addCourse', data);
}

// 查询所有教师
export function getAllTeachers() {
  return axios.get('/api/user/getAllteacher');
}

// 获取所有课程列表
export function getCourseList() {
  return axios.get('/api/admincourse/getCourseList');
}

// 获取课程详情（根据ID）
export function getCourseById(id: string | number) {
  return axios.get(`/api/admincourse/getCourseById/${id}`);
}

// 修改课程
export function updateCourse(id: string, data: { name: string; description: string; teacherId: string }) {
  return axios.post(`/api/admincourse/updateCourse/${id}`, data);
}

// 删除课程（支持批量）
export function deleteCourse(ids: string[]) {
  return axios.get(`/api/admincourse/deleteCourse`, {
    params: { ids: ids.join(',') }
  });
}

// 获取学生加入课程的请求列表（管理员/教师）
export function getJoinCourseRequestList(courseId: string | number) {
  return axios.get(`/api/admincourse/joinCourserequest/${courseId}`);
}

// 管理员接受/拒绝学生加入课程的请求
export function acceptJoinRequest(requestId: string | number, status: 'APPROVED' | 'REJECTED') {
  return axios.get(`/api/admincourse/acceptJoinRequest/${requestId}?status=${status}`);
}

// ===================== 学生接口 =====================

// 获取学生加入的课程列表
export function getStudentCourseList(studentId: string | number) {
  return axios.get(`/api/studentcourse/getCourseList/${studentId}`);
}

// 学生申请加入课程
export function joinCourse(courseId: string | number) {
  return axios.get(`/api/studentcourse/joinCourse/${courseId}`);
}

// ===================== 教师接口 =====================

// 教师上传课程内容
export function uploadCourseContent(file: File, courseId: string | number) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('courseId', String(courseId));
  return axios.post('/api/teachercourse/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

// 获取教师课程列表
export function getTeacherCourseList(teacherId: string | number) {
  return axios.get(`/api/teachercourse/getCourseList/${teacherId}`);
}