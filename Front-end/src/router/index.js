import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import SignupView from '../views/SignupView.vue'
import MainLayout from '../layouts/MainLayout.vue' // 레이아웃 파일
import { isAuthenticated } from '@/auth/session'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },

    {
      path: '/main',
      component: MainLayout,
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('../views/Dashboard.vue'),
        },
        {
          path: '/analysis', // 💡 추가된 경로
          name: 'analysis',
          component: () => import('../views/Analysis.vue'), // 이 파일을 만들어야 함
        },
        {
          path: '/V2X',
          name: 'V2X',
          component: () => import('../views/V2X.vue'), // views/V2X.vue 파일을 불러옴
        },
        {
          path: '/v2x',
          redirect: '/V2X',
        },
        {
          path: '/Recommend', // 추가된 경로
          name: 'Recommend',
          component: () => import('../views/RecommendView.vue'),
        },
        {
          path: '/recommend',
          redirect: '/Recommend',
        },
      ],
    },
  ],
})

const protectedPaths = ['/dashboard', '/analysis', '/V2X', '/v2x', '/Recommend', '/recommend']

router.beforeEach((to) => {
  if (protectedPaths.includes(to.path) && !isAuthenticated()) {
    return '/'
  }

  if ((to.path === '/' || to.path === '/signup') && isAuthenticated()) {
    return '/dashboard'
  }

  return true
})

export default router
