import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import homePage from '@/views/homePage.vue'
import AboutPage from '@/views/aboutPage.vue'
import LoginPage from '@/views/loginPage.vue'
import RegisterPage from '@/views/registerPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: BasicLayout,
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: '首页',
          component: homePage,
        },
        {
          path: 'about',
          name: '关于',
          component: AboutPage,
        },
        {
          path: 'login',
          name: '登录',
          component: LoginPage,
        },
        {
          path: 'register',
          name: '注册',
          component: RegisterPage,
        },
      ],
    },
  ],
})

export default router
