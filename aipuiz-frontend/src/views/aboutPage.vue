<template>
  <div class="about-root">
    <div class="card about-card">
      <h2>个人主页</h2>
      <div v-if="user">
        <p>账号：{{ user.userAccount }}</p>
        <p>角色：{{ user.userRole === 'student' ? '学生' : user.userRole === 'teacher' ? '教师' : user.userRole === 'admin' ? '管理员' : user.userRole }}</p>
      </div>
      <button @click="handleLogout" class="logout-btn">退出登录</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { computed } from 'vue'

const user = computed(() => {
  const u = localStorage.getItem('user')
  return u ? JSON.parse(u) : null
})

const handleLogout = async () => {
  try {
    // 发送 POST 请求到后端退出登录接口
    await axios.post('/api/user/logout', {}, { withCredentials: true })

    // 清除本地用户信息
    localStorage.removeItem('user');
    window.location.href = '/home'; // 强制刷新页面

    alert('已成功退出登录')
    // 跳转到首页或登录页，刷新页面状态
  } catch (error) {
    console.error('退出登录失败:', error)
    alert('退出登录失败，请重试')
  }
}
</script>

<style scoped>
.about-root {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.card.about-card {
  background: rgba(255,255,255,0.95);
  border-radius: 18px;
  
  /* 改进的阴影效果 - 增加Y轴偏移和模糊度 */
  box-shadow: 
    0 12px 24px -6px rgba(31, 38, 135, 0.15),
    0 4px 12px rgba(0, 0, 0, 0.05);
  
  /* 添加内阴影增加深度 */
  box-shadow: 
    inset 0 1px 2px rgba(255,255,255,0.3),
    0 12px 24px -6px rgba(31, 38, 135, 0.15),
    0 4px 12px rgba(0, 0, 0, 0.05);
  
  padding: 24px 20px;
  max-width: 300px;
  width: 90%;
  margin: 40px auto;
  text-align: center;
  
  /* 添加微妙的边框渐变 */
  border: 1px solid rgba(255,255,255,0.3);
  
  /* 添加悬停效果增强交互感 */
  transition: all 0.3s ease;
  transform: translateY(0);
}

.card.about-card:hover {
  transform: translateY(-4px);
  box-shadow: 
    0 16px 32px -8px rgba(31, 38, 135, 0.2),
    0 8px 16px rgba(0, 0, 0, 0.1);
}

.logout-btn {
  background: #f56c6c;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  margin-top: 18px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(245,108,108,0.12);
}
.logout-btn:hover {
  background: #d9534f;
}
</style>
