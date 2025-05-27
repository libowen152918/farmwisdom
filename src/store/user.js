import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const loading = ref(false)

  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ROLE_ADMIN')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    if (newToken) {
      axios.defaults.headers['Authorization'] = `Bearer ${newToken}`
    } else {
      delete axios.defaults.headers['Authorization']
    }
  }

  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
    delete axios.defaults.headers['Authorization']
  }

  const login = async (credentials) => {
    try {
      loading.value = true
      console.log('Sending login request to /auth/login')
      const { data } = await axios.post('/auth/login', {
        username: credentials.username,
        password: credentials.password
      })
      console.log('Login successful, received token and user data')
      setToken(data.token)
      user.value = data
      ElMessage.success('登录成功')
      return data
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const register = async (userData) => {
    try {
      loading.value = true
      console.log('Sending register request to /auth/register with data:', userData)
      const { data } = await axios.post('/auth/register', userData)
      ElMessage.success('注册成功')
      return data
    } catch (error) {
      console.error('Registration error:', error)
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message)
      }
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    user.value = null
    clearToken()
    ElMessage.success('已退出登录')
  }

  const resetPassword = async (email) => {
    try {
      loading.value = true
      await axios.post('/api/users/reset-password', { email })
      ElMessage.success('重置密码邮件已发送')
    } finally {
      loading.value = false
    }
  }

  const confirmResetPassword = async (token, newPassword) => {
    try {
      loading.value = true
      await axios.post('/api/users/confirm-reset-password', {
        token,
        newPassword
      })
      ElMessage.success('密码重置成功')
    } finally {
      loading.value = false
    }
  }

  const changePassword = async (oldPassword, newPassword) => {
    try {
      loading.value = true
      await axios.put('/api/users/me/password', { oldPassword, newPassword })
      ElMessage.success('密码修改成功')
    } finally {
      loading.value = false
    }
  }

  const updateProfile = async (profileData) => {
    try {
      loading.value = true
      const { data } = await axios.put('/api/users/me', profileData)
      user.value = data
      ElMessage.success('个人信息更新成功')
      return data
    } finally {
      loading.value = false
    }
  }

  const updateAvatar = async (file) => {
    try {
      loading.value = true
      const formData = new FormData()
      formData.append('file', file)
      const { data } = await axios.post('/api/users/me/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      user.value.avatar = data
      ElMessage.success('头像更新成功')
      return data
    } finally {
      loading.value = false
    }
  }

  const getCurrentUser = async () => {
    if (!token.value) return null
    
    try {
      loading.value = true
      const { data } = await axios.get('/users/me')
      user.value = data
      return data
    } catch (error) {
      if (error.response?.status === 401) {
        logout()
      }
      throw error
    } finally {
      loading.value = false
    }
  }

  // 初始化时设置 token
  if (token.value) {
    setToken(token.value)
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    resetPassword,
    confirmResetPassword,
    changePassword,
    updateProfile,
    updateAvatar,
    getCurrentUser
  }
}) 