<template>
  <div class="post-list-container">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="category-filter">
        <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="handleCategoryChange">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </div>
      
      <!-- 添加搜索框 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索帖子标题"
          prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <div class="actions">
        <el-button v-if="userStore.isAuthenticated" type="primary" @click="handleCreatePost">
          <el-icon><Plus /></el-icon>发表帖子
        </el-button>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="posts-container">
      <el-empty v-if="!loading && (!posts || posts.length === 0)" description="暂无帖子" />
      
      <el-skeleton v-else-if="loading" :rows="5" animated />
      
      <div v-else class="post-list">
        <div v-for="post in posts" :key="post.id" class="post-item" @click="viewPost(post.id)">
          <!-- 精品标识 -->
          <div v-if="post.isEssence" class="essence-badge">
            <el-icon color="#fff"><Star /></el-icon>
          </div>
          
          <div class="post-header">
            <h3 class="post-title">{{ post.title }}</h3>
            <div class="post-tags">
              <el-tag v-if="post.isTop" type="danger" effect="dark" size="small">置顶</el-tag>
              <el-tag size="small" type="info">{{ getCategoryName(post.categoryId) }}</el-tag>
            </div>
          </div>
          
          <div class="post-content">{{ post.content }}</div>
          
          <div class="post-footer">
            <div class="post-info">
              <span class="author">{{ post.author }}</span>
              <span class="time">{{ formatDate(post.createTime) }}</span>
            </div>
            <div class="post-stats">
              <span class="views">
                <el-icon><View /></el-icon>
                {{ post.views }}
              </span>
              <span class="comments">
                <el-icon><ChatLineRound /></el-icon>
                {{ post.commentCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { usePostStore } from '@/store/posts'
import { ElMessage } from 'element-plus'
import { Plus, View, ChatLineRound, Search, Star } from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const postStore = usePostStore()

const loading = ref(false)
const posts = ref([])
const allPosts = ref([]) // 存储所有帖子
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const categories = ref([])
const selectedCategory = ref(null)
const searchQuery = ref('')

// 分类路径映射
const categoryPathMap = {
  'tech': 1,
  'disease': 2,
  'market': 3,
  'experience': 4,
  'policy': 5,
  'tools': 6
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

// 加载帖子列表
const loadPosts = async () => {
  loading.value = true
  try {
    const response = await axios.get('/posts', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        categoryId: selectedCategory.value
      }
    })
    if (response?.data?.records) {
      allPosts.value = response.data.records
      // 如果有搜索关键词，过滤帖子
      if (searchQuery.value?.trim()) {
        const filteredPosts = allPosts.value.filter(post => 
          post.title.toLowerCase().includes(searchQuery.value.trim().toLowerCase())
        )
        posts.value = filteredPosts
        total.value = filteredPosts.length
      } else {
        posts.value = allPosts.value
        total.value = response.data.total
      }
    } else {
      posts.value = []
      allPosts.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取帖子列表失败:', error)
    ElMessage.error('获取帖子列表失败')
    posts.value = []
    allPosts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 监听路由变化
watch(() => route.path, (newPath) => {
  const pathSegments = newPath.split('/')
  if (pathSegments[1] === 'forum' && pathSegments[2]) {
    const categoryId = categoryPathMap[pathSegments[2]]
    if (categoryId) {
      selectedCategory.value = categoryId
    } else {
      selectedCategory.value = null
    }
  } else {
    selectedCategory.value = null
  }
  currentPage.value = 1
  loadPosts()
}, { immediate: true })

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 查看帖子详情
const viewPost = (id) => {
  router.push(`/forum/post/${id}`)
}

// 创建新帖子
const handleCreatePost = () => {
  router.push('/forum/create')
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  if (searchQuery.value?.trim()) {
    // 如果有搜索关键词，手动处理分页
    const start = (currentPage.value - 1) * val
    const end = start + val
    const filteredPosts = allPosts.value.filter(post => 
      post.title.toLowerCase().includes(searchQuery.value.trim().toLowerCase())
    )
    posts.value = filteredPosts.slice(start, end)
  } else {
    loadPosts()
  }
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  if (searchQuery.value?.trim()) {
    // 如果有搜索关键词，手动处理分页
    const start = (val - 1) * pageSize.value
    const end = start + pageSize.value
    const filteredPosts = allPosts.value.filter(post => 
      post.title.toLowerCase().includes(searchQuery.value.trim().toLowerCase())
    )
    posts.value = filteredPosts.slice(start, end)
  } else {
    loadPosts()
  }
}

// 处理分类变化
const handleCategoryChange = () => {
  currentPage.value = 1
  searchQuery.value = '' // 清空搜索框
  loadPosts()
}

// 处理搜索
const handleSearch = () => {
  if (searchQuery.value?.trim()) {
    // 在现有数据中搜索
    const filteredPosts = allPosts.value.filter(post => 
      post.title.toLowerCase().includes(searchQuery.value.trim().toLowerCase())
    )
    posts.value = filteredPosts
    total.value = filteredPosts.length
    currentPage.value = 1
  } else {
    // 如果搜索框为空，显示所有帖子
    posts.value = allPosts.value
    total.value = allPosts.value.length
  }
}

// 组件挂载时加载数据
onMounted(async () => {
  await fetchCategories()
  await loadPosts()
})
</script>

<style scoped>
.post-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.search-bar {
  flex: 1;
  max-width: 400px;
}

.posts-container {
  margin-bottom: 20px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-item {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.post-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.essence-badge {
  position: absolute;
  top: -20px;
  left: -20px;
  width: 40px;
  height: 40px;
  background-color: #E6A23C;
  transform: rotate(45deg);
  display: flex;
  justify-content: center;
  align-items: center;
}

.essence-badge .el-icon {
  transform: rotate(-45deg);
  margin-top: 25px;
  margin-left: 5px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.post-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.post-content {
  color: #606266;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #909399;
}

.post-info {
  display: flex;
  gap: 12px;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.views, .comments {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 