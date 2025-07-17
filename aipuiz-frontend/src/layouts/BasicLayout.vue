<template>
  <div class="common-layout">
    <el-container>
      <el-header>AI-quiz</el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="route.path"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
          >
            <el-menu-item index="/home" @click="navigateTo('/home')">
              <el-icon><setting /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/about" @click="navigateTo('/about')">
              <el-icon><setting /></el-icon>
              <span>关于</span>
            </el-menu-item>
            <el-menu-item index="/login" @click="navigateTo('/login')">
              <el-icon><setting /></el-icon>
              <span>登录</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main> <RouterView :key="$route.fullPath" /></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { ref } from 'vue'
import { onMounted, watch } from 'vue'

const router = useRouter()
const route = useRoute()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.el-aside {
  background: #ededf4 !important;
  border-radius: 0 0 18px 18px;
  min-height: 100vh;
  padding-top: 0;
}

.el-menu-vertical-demo {
  background: #ededf4 !important;
  border-right: none;
  box-shadow: none;
}

.el-menu-vertical-demo .el-menu-item {
  background: #ededf4 !important;
  color: #333 !important;
  border-radius: 0;
  margin: 0;
  transition: background 0.2s, color 0.2s;
  border-bottom: 1px solid rgb(207, 209, 211);
}



.el-menu-vertical-demo .el-menu-item.is-active {
  background: #8ea4ca !important;
  color:rgb(80, 126, 138) !important;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(143,182,233,0.10);
}

.el-menu-vertical-demo .el-menu-item.is-active .el-icon {
  color:rgb(32, 187, 235) !important;
}
</style>
