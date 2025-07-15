import axios from 'axios'

/**
 * 用户登录
 * @param {Object} data - 登录表单数据 { userAccount, userPassword }
 * @returns {Promise}
 */
export function loginUser(data) {
  return axios.post('/api/user/login', data)
}

/**
 * 用户注册
 * @param {Object} data - 注册表单数据 { userRole, userAccount, userPassword, checkPassword }
 * @returns {Promise}
 */
export function registerUser(data) {
  return axios.post('/api/user/register', data)
}

/**
 * 获取所有教师列表
 * @returns {Promise}
 */
export function getTeacherList() {
  return axios.get('/api/user/getAllteacher')
}
