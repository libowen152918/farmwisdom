import { defineStore } from 'pinia'
import axios from '@/utils/axios'  // 使用自定义的 axios 实例
import { ElMessage } from 'element-plus'

export const usePostStore = defineStore('posts', {
  state: () => ({
    posts: [],
    currentPost: null,
    loading: false,
    error: null,
    total: 0,
    currentPage: 1,
    pageSize: 10
  }),

  getters: {
    getPosts: (state) => state.posts,
    getCurrentPost: (state) => state.currentPost
  },

  actions: {
    async fetchPosts(page = 1, pageSize = 10, categoryId = null) {
      this.loading = true
      this.error = null
      
      try {
        const params = {
          page: page - 1,  // 转换为从0开始的页码
          size: pageSize,
          ...(categoryId && { categoryId })
        }
        
        console.log('Fetching posts with params:', params)
        
        const response = await axios.get('/posts', { params })
        console.log('Received posts response:', response.data)
        
        if (response.data && Array.isArray(response.data.records)) {
          this.posts = response.data.records
          this.total = response.data.total
          this.currentPage = page
          this.pageSize = pageSize
          
          // 添加更详细的日志
          console.log('Updated posts in store:', {
            posts: this.posts,
            total: this.total,
            currentPage: this.currentPage,
            pageSize: this.pageSize,
            params: params
          })
        } else {
          console.error('Invalid response format:', response.data)
          this.error = '数据格式错误'
          this.posts = []
          this.total = 0
        }
      } catch (error) {
        console.error('获取帖子列表失败:', error)
        this.error = '获取帖子列表失败'
        ElMessage.error('获取帖子失败')
        this.posts = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    async fetchPostById(id) {
      this.loading = true
      try {
        const response = await axios.get(`/posts/${id}`)
        this.currentPost = response.data
        return response.data
      } catch (error) {
        console.error('获取帖子详情失败:', error)
        this.error = error.message
        this.currentPost = null
        throw error
      } finally {
        this.loading = false
      }
    },

    async createPost(postData) {
      try {
        const response = await axios.post('/posts', postData)
        ElMessage.success('发布成功')
        return response.data
      } catch (error) {
        console.error('发布帖子失败:', error)
        ElMessage.error(error.response?.data?.message || '发布失败')
        throw error
      }
    },

    async updatePost(id, postData) {
      this.loading = true
      try {
        const response = await axios.put(`/posts/${id}`, postData)
        const index = this.posts.findIndex(post => post.id === id)
        if (index !== -1) {
          this.posts[index] = response.data
        }
        return response.data
      } catch (error) {
        console.error('更新帖子失败:', error)
        ElMessage.error(error.response?.data?.message || '更新失败')
        throw error
      } finally {
        this.loading = false
      }
    },

    async deletePost(id) {
      try {
        await axios.delete(`/posts/${id}`)
        this.posts = this.posts.filter(post => post.id !== id)
        this.total--
        ElMessage.success('删除成功')
      } catch (error) {
        console.error('删除帖子失败:', error)
        ElMessage.error(error.response?.data?.message || '删除失败')
        throw error
      }
    }
  }
}) 