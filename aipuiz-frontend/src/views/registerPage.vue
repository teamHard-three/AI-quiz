<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="userAccount">用户名:</label>
        <input
          type="text"
          id="userAccount"
          v-model="formData.userAccount"
          required
          placeholder="请输入用户名"
        />
      </div>
      <div class="form-group">
        <label for="userPassword">密码:</label>
        <input
          type="password"
          id="userPassword"
          v-model="formData.userPassword"
          required
          placeholder="请输入密码"
        />
      </div>
      <div class="form-group">
        <label for="checkPassword">确认密码:</label>
        <input
          type="password"
          id="checkPassword"
          v-model="formData.checkPassword"
          required
          placeholder="请再次输入密码"
        />
      </div>
      <div class="form-group">
        <label>身份:</label>
        <label><input type="radio" value="student" v-model="formData.userRole" /> 学生</label>
        <label><input type="radio" value="teacher" v-model="formData.userRole" /> 老师</label>
        <label><input type="radio" value="admin" v-model="formData.userRole" /> 管理员</label>
      </div>
      <button type="submit" class="register-button">注册</button>
    </form>
    <div style="text-align:center; margin-top: 10px;">
      <RouterLink to="/login">已有账号？去登录</RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { RouterLink } from 'vue-router'
import axios from 'axios'
import { registerUser } from '@/api/user'

const router = useRouter()
const formData = ref({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
  userRole: 'student', // 默认角色
})

const handleRegister = async () => {
  if (formData.value.userPassword !== formData.value.checkPassword) {
    alert('两次输入的密码不一致')
    return
  }
  try {
    const response = await registerUser(formData.value)
    console.log('注册返回：', response)
    if (response.data && response.data.code === 0) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      alert((response.data && response.data.message) || '注册失败')
    }
  } catch (error) {
    alert('请求失败')
  }
}
</script>

<style scoped>
body,
#app {
  min-height: 100vh;
  margin: 0;
  padding: 0;
  background-color: #e0c3fc;
  background-image: linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%);
}

.register-container {
  max-width: 400px;
  margin: 60px auto;
  padding: 40px 32px 32px 32px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.18);
  backdrop-filter: blur(4px);
  display: flex;
  flex-direction: column;
  align-items: center;
}

h2 {
  text-align: center;
  color: #6c63ff;
  font-weight: bold;
  margin-bottom: 30px;
  letter-spacing: 8px;
  font-size: 2rem;
}

.form-group {
  width: 100%;
  margin-bottom: 22px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 8px;
  color: #6c63ff;
}

input[type='text'],
input[type='password'] {
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-bottom: 2px solid #bdbdbd;
  background: transparent;
  outline: none;
  font-size: 1rem;
  margin-bottom: 2px;
  transition: border-color 0.3s;
}

input[type='text']:focus,
input[type='password']:focus {
  border-bottom: 2px solid #6c63ff;
}

.register-button {
  width: 100%;
  padding: 12px;
  background-image: linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
  transition: background 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(140, 198, 252, 0.15);
}

.register-button:hover {
  background-image: linear-gradient(120deg, #8ec5fc 0%, #e0c3fc 100%);
  box-shadow: 0 4px 16px rgba(140, 198, 252, 0.25);
}

a,
.router-link-active {
  color: #6c63ff;
  text-decoration: none;
  font-size: 0.95rem;
  margin: 0 8px;
}

a:hover {
  text-decoration: underline;
}
</style>
