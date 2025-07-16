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

<style scoped></style>
