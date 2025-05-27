import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/',
    redirect: '/forum'
  },
  {
    path: '/forum',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/tech',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/disease',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/market',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/experience',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/policy',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/tools',
    component: () => import('@/views/Forum/PostList.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/post/:id',
    component: () => import('@/views/Forum/PostDetail.vue'),
    meta: { requiresGuest: false }
  },
  {
    path: '/forum/create',
    component: () => import('@/views/Forum/CreatePost.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/forum/my',
    component: () => import('@/views/Forum/MyPosts.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/auth/ForgotPassword.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/auth/ResetPassword.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/Admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        component: () => import('@/views/Admin/AdminDashboard.vue')
      },
      {
        path: 'users',
        component: () => import('@/views/Admin/UserManagement.vue')
      },
      {
        path: 'posts',
        component: () => import('@/views/Admin/PostManagement.vue')
      },
      {
        path: 'categories',
        component: () => import('@/views/Admin/CategoryManagement.vue')
      },
      {
        path: 'crops',
        component: () => import('@/views/Admin/CropManagement.vue')
      },
      {
        path: 'weather',
        component: () => import('@/views/Admin/WeatherManagement.vue')
      }
    ]
  },
  {
    path: '/forum/edit/:id',
    component: () => import('@/views/Forum/EditPost.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  try {
    if (userStore.token && !userStore.user) {
      try {
        await userStore.getCurrentUser()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        userStore.clearToken()
        if (to.meta.requiresAuth || to.meta.requiresAdmin) {
          next({ path: '/login', query: { redirect: to.fullPath } })
          return
        }
      }
    }

    if (to.meta.requiresAuth && !userStore.isAuthenticated) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }

    if (to.meta.requiresGuest && userStore.isAuthenticated) {
      next('/')
      return
    }

    if (to.meta.requiresAdmin) {
      if (!userStore.isAuthenticated) {
        next({ path: '/login', query: { redirect: to.fullPath } })
        return
      }
      if (!userStore.isAdmin) {
        next('/forum')
        return
      }
    }

    next()
  } catch (error) {
    console.error('路由守卫错误:', error)
    userStore.clearToken()
    next({ path: '/login', query: { redirect: to.fullPath } })
  }
})

export default router 