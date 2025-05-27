import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import { useUserStore } from '@/store/user'

// 样式导入
import 'element-plus/dist/index.css'
import './style.css'

// 导入组件
import App from './App.vue'
import router from './router'

// 配置 axios
axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
axios.defaults.timeout = 10000
axios.defaults.headers.common['Content-Type'] = 'application/json'

// 创建应用实例
const app = createApp(App)

// 创建 Pinia 实例
const pinia = createPinia()
app.use(pinia)

// 添加响应拦截器
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      if (userStore) {
        userStore.clearToken()
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(router)
app.use(ElementPlus)

// 挂载应用
app.mount('#app') 