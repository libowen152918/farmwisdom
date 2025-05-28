import axios from '@/utils/axios'

// 获取帖子列表（支持搜索）
export function getPostList(params) {
  return axios.get('/posts/search', {
    params: {
      page: params.page || 1,
      size: params.size || 10,
      categoryId: params.categoryId,
      search: params.search?.trim()
    }
  })
}

// 获取帖子详情
export function getPostDetail(id) {
  return axios.get(`/posts/${id}`)
}

// 创建帖子
export function createPost(data) {
  return axios.post('/posts', data)
}

// 更新帖子
export function updatePost(id, data) {
  return axios.put(`/posts/${id}`, data)
}

// 删除帖子
export function deletePost(id) {
  return axios.delete(`/posts/${id}`)
} 