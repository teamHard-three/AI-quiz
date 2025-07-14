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
.register-container {
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

.register-button {
  width: 100%;
  padding: 10px;
  background-color: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.register-button:hover {
  background-color: #409eff;
}
</style>
