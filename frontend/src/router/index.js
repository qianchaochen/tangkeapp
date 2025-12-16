import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/splash'
  },
  {
    path: '/splash',
    name: 'Splash',
    component: () => import('../../pages/splash/splash.vue')
  },
  {
    path: '/index',
    name: 'Index',
    component: () => import('../../pages/index/index.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
