<template>
  <div>
    <h2>欢迎来到关于</h2>
    <p>这里是关于内容。</p>
    <button @click="handleLogout" class="logout-button">退出登录</button>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'

const handleLogout = async () => {
  try {
    // 发送 POST 请求到后端退出登录接口
    await axios.post('/api/user/logout', {}, { withCredentials: true })

    // 清除本地用户信息（如果有的话）
    // localStorage.removeItem('user');

    alert('已成功退出登录')
    // 可选：跳转到登录页或其他页面
    // window.location.href = '/login';
  } catch (error) {
    console.error('退出登录失败:', error)
    alert('退出登录失败，请重试')
  }
}
</script>

<style scoped>
.logout-button {
  margin-top: 20px;
  padding: 10px 15px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-button:hover {
  background-color: #c82333;
}
</style>
