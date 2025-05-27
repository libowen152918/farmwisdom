import request from '@/utils/request'

// 天气管理相关 API
export function getWeatherList(page = 1, size = 10, location = '') {
  return request({
    url: '/admin/weather',
    method: 'get',
    params: {
      page,
      size,
      location
    }
  })
}

export function addWeather(data) {
  return request({
    url: '/admin/weather',
    method: 'post',
    data
  })
}

export function updateWeather(id, data) {
  return request({
    url: `/admin/weather/${id}`,
    method: 'put',
    data
  })
}

export function deleteWeather(id) {
  return request({
    url: `/admin/weather/${id}`,
    method: 'delete'
  })
}

// 作物管理相关 API
export function getCropList(page = 1, size = 10) {
  return request({
    url: '/admin/crops',
    method: 'get',
    params: {
      page,
      size
    }
  })
}

export function addCrop(data) {
  return request({
    url: '/admin/crops',
    method: 'post',
    data
  })
}

export function updateCrop(id, data) {
  return request({
    url: `/admin/crops/${id}`,
    method: 'put',
    data
  })
}

export function deleteCrop(id) {
  return request({
    url: `/admin/crops/${id}`,
    method: 'delete'
  })
}

// 用户管理相关 API
export function getUserList(page = 1, size = 10) {
  return request({
    url: '/admin/users',
    method: 'get',
    params: {
      page,
      size
    }
  })
}

export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export function resetUserPassword(id) {
  return request({
    url: `/admin/users/${id}/reset-password`,
    method: 'post'
  })
}

export function toggleUserStatus(id, enabled) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { enabled }
  })
}

// 获取统计数据
export function getStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  });
}

// 获取用户角色选项
export function getRoleOptions() {
  return request({
    url: '/admin/roles',
    method: 'get'
  });
} 