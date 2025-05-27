<template>
  <div class="post-management">
    <div class="management-header">
      <h2>帖子管理</h2>
      <div class="search-bar">
        <el-select v-model="selectedCategory" placeholder="选择分类" clearable class="category-select" @change="handleCategoryChange">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        <el-input
          v-model="searchQuery"
          placeholder="搜索帖子标题"
          prefix-icon="el-icon-search"
          @input="handleSearch"
        />
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="posts"
      style="width: 100%"
      border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="{ row }">
          <router-link :to="'/forum/post/' + row.id" class="post-link">
            {{ row.title }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column label="分类" width="120">
        <template #default="{ row }">
          {{ getCategoryName(row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? '正常' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.status === 'active' ? 'danger' : 'success'"
            @click="togglePostStatus(row)"
          >
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="deletePost(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

const route = useRoute()
const posts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')
const categories = ref([])
const selectedCategory = ref(null)

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const response = await axios.get('/admin/posts', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        search: searchQuery.value,
        categoryId: selectedCategory.value
      }
    })
    // 假设后端返回的数据结构是 { records: [...], total: ... }
    if (response && response.data && Array.isArray(response.data.records)) {
      posts.value = response.data.records
      total.value = response.data.total
    } else {
       console.warn('获取的帖子列表数据格式不正确或为空', response);
       posts.value = []
       total.value = 0
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
    console.error('Error fetching posts:', error)
    posts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  currentPage.value = 1
  fetchPosts()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPosts()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchPosts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchPosts()
}

const togglePostStatus = async (post) => {
  try {
    await axios.patch(`/admin/posts/${post.id}/status`, {
      status: post.status === 'active' ? 'disabled' : 'active'
    })
    await fetchPosts()
    ElMessage.success('更新帖子状态成功')
  } catch (error) {
    ElMessage.error('更新帖子状态失败')
    console.error('Error updating post status:', error)
  }
}

const deletePost = async (post) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？此操作不可逆', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/admin/posts/${post.id}`)
    await fetchPosts()
    ElMessage.success('删除帖子成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除帖子失败')
      console.error('Error deleting post:', error)
    }
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

// 监听路由变化
watch(() => route.query.category, (newCategory) => {
  if (newCategory) {
    selectedCategory.value = parseInt(newCategory)
  } else {
    selectedCategory.value = null
  }
  currentPage.value = 1
  fetchPosts()
})

onMounted(async () => {
  await fetchCategories()
  if (route.query.category) {
    selectedCategory.value = parseInt(route.query.category)
  }
  fetchPosts()
})
</script>

<style scoped>
.post-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  width: 500px;
}

.category-select {
  width: 200px;
}

.post-link {
  color: #1a472a;
  text-decoration: none;
}

.post-link:hover {
  color: #2c6a3f;
  text-decoration: underline;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
}

:deep(.el-button) {
  margin-right: 8px;
}
</style> 