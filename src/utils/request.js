import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 从环境变量获取 API 基础路径
  timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果响应成功，直接返回数据
    if (response.status === 200) {
      return res
    }
    
    // 处理其他状态码
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理 401 未授权错误
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore()
      userStore.clearToken()
      window.location.href = '/login'
      ElMessage.error('登录已过期，请重新登录')
    } else {
      // 处理其他错误
      ElMessage.error(error.response?.data?.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

export default service 