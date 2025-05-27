import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/utils/axios'

export const useUserStore = defineStore('userStore', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const username = computed(() => user.value?.username || '')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    if (newToken) {
      axios.defaults.headers['Authorization'] = `Bearer ${newToken}`
    } else {
      delete axios.defaults.headers['Authorization']
    }
  }

  const login = async (credentials) => {
    try {
      const { data } = await axios.post('/auth/login', credentials)
      setToken(data.token)
      user.value = data
      return data
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    }
  }

  const logout = () => {
    user.value = null
    setToken('')
  }

  return {
    token,
    user,
    isAuthenticated,
    username,
    login,
    logout
  }
})
