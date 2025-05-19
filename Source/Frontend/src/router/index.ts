import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../store/user'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'Home', component: () => import('../pages/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('../pages/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../pages/Register.vue') },
  { path: '/settings', name: 'Settings', component: () => import('../pages/Settings.vue'), meta: { requiresAuth: true } },
  { path: '/admin', name: 'Admin', component: () => import('../pages/Admin.vue'), meta: { requiresAuth: true } },
  { path: '/messages', name: 'Messages', component: () => import('../pages/Messages.vue'), meta: { requiresAuth: true } },
  { path: '/posts/create', name: 'CreatePost', component: () => import('../pages/CreatePost.vue'), meta: { requiresAuth: true } },
  { path: '/profile/:username', name: 'Profile', component: () => import('../pages/Profile.vue') },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('../pages/NotFound.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation guard for protected routes
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router 