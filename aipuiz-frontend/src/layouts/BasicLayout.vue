<template>
  <div class="common-layout">
    <el-container>
      <el-header>AI-quiz</el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="activeIndex"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
          >
            <el-menu-item index="1" @click="navigateTo('/home')">
              <el-icon><setting /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="2" @click="navigateTo('/about')">
              <el-icon><setting /></el-icon>
              <span>关于</span>
            </el-menu-item>
            <el-menu-item index="3" @click="navigateTo('/login')">
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
import { RouterLink, RouterView } from 'vue-router'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { onMounted, watch } from 'vue'
const router = useRouter()

const activeIndex = ref('1')
const navigateTo = (path) => {
  router.push(path)
}
const setActiveIndexByRoute = () => {
  const path = router.currentRoute.value.path
  if (path === '/home') {
    activeIndex.value = '1'
  } else if (path === '/about') {
    activeIndex.value = '2'
  } else if (path === '/login') {
    activeIndex.value = '3'
  }
}
watch(
  () => router.path,
  () => {
    setActiveIndexByRoute()
  },
)
onMounted(() => {
  router.push('/home')
  setActiveIndexByRoute()
})
</script>

<style scoped></style>
