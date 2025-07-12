<template>
  <div class="login-container">
    <h2>用户登录</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="username">用户名:</label>
        <input
          type="text"
          id="username"
          v-model="formData.userAccount"
          required
          placeholder="请输入用户名"
        />
      </div>

      <div class="form-group">
        <label for="password">密码:</label>
        <input
          type="password"
          id="password"
          v-model="formData.userPassword"
          required
          placeholder="请输入密码"
        />
      </div>

      <button type="submit" class="login-button">登录</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
// 定义表单数据模型
interface FormData {
  userAccount: string
  userPassword: string
}
import { RouterLink, RouterView } from 'vue-router'
import { useRouter } from 'vue-router'
const router = useRouter()
const formData = ref<FormData>({
  userAccount: '',
  userPassword: '',
})

// 登录处理函数
const handleLogin = async () => {
  try {
    const response = await axios.post('/api/user/login', formData.value)

    // 登录成功处理逻辑
    console.log('登录成功，响应数据:', response.data)

    // 可选：跳转到首页或其他页面
    router.push('/about')
  } catch (error) {
    // 错误处理逻辑
    console.error('登录失败:', error)

    // 提示用户登录失败
    alert('登录失败，请检查您的用户名和密码')
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

input[type='text'],
input[type='password'] {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.login-button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.login-button:hover {
  background-color: #0056b3;
}
</style>
