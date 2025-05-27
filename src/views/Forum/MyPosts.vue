<template>
  <div class="my-posts">
    <el-card>
      <template #header>
        <div class="header">
          <h2>我的帖子</h2>
          <el-button type="primary" @click="$router.push('/forum/create')" class="create-post-btn">
            <el-icon><Edit /></el-icon>
            发布新帖子
          </el-button>
        </div>
      </template>

      <el-table 
        v-loading="loading" 
        :data="myPosts" 
        style="width: 100%"
        :empty-text="emptyText"
      >
        <el-table-column prop="title" label="标题">
          <template #default="{ row }">
            <router-link :to="`/forum/post/${row.id}`">{{ row.title }}</router-link>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              @click="handleEdit(row.id)"
              class="action-button"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row.id)"
              class="action-button"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="total > 0">
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const router = useRouter()
const loading = ref(false)
const myPosts = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const emptyText = ref('暂无帖子，快去发布一篇吧！')

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const fetchMyPosts = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push('/login')
      return
    }
    
    console.log('Fetching posts with params:', {
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    const response = await axios.get('/posts/my', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })
    
    console.log('Received response:', response.data)
    
    if (response.data && Array.isArray(response.data.records)) {
      myPosts.value = response.data.records
      total.value = response.data.total
      console.log('Updated posts:', myPosts.value)
      console.log('Total posts:', total.value)
    } else {
      console.error('Invalid response format:', response.data)
      ElMessage.error('数据格式错误')
    }
  } catch (error) {
    console.error('获取我的帖子失败:', error)
    ElMessage.error('获取我的帖子失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchMyPosts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchMyPosts()
}

const handleEdit = (id) => {
  router.push(`/forum/edit/${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', {
      type: 'warning'
    })
    await axios.delete(`/posts/${id}`)
    ElMessage.success('删除成功')
    fetchMyPosts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error)
      ElMessage.error('删除帖子失败')
    }
  }
}

onMounted(() => {
  fetchMyPosts()
})
</script>

<style scoped>
.my-posts {
  padding: var(--spacing-lg);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  color: var(--primary-color);
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  position: relative;
}

.header h2::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 40px;
  height: 3px;
  background-color: var(--primary-color);
  border-radius: 2px;
  animation: scaleIn var(--transition-normal) var(--ease-out-expo) 0.5s;
}

.create-post-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  font-weight: 500;
  background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
  border: none;
  transition: all var(--transition-fast) var(--ease-out-expo);
}

.create-post-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.2);
}

.action-button {
  margin: 0 4px;
  transition: all var(--transition-fast) var(--ease-out-expo);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

:deep(.el-table) {
  margin-top: var(--spacing-lg);
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

:deep(.el-table__row) {
  transition: all var(--transition-fast) var(--ease-out-expo);
}

:deep(.el-table__row:hover) {
  transform: translateX(5px);
  background: linear-gradient(
    90deg,
    rgba(46, 125, 50, 0.05) 0%,
    rgba(46, 125, 50, 0.02) 100%
  ) !important;
}

.pagination-container {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-md) 0;
  display: flex;
  justify-content: center;
}

a {
  color: var(--primary-color);
  text-decoration: none;
  transition: all var(--transition-fast) var(--ease-out-expo);
  padding: 2px 4px;
  position: relative;
}

a::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 2px;
  background-color: var(--primary-color);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform var(--transition-normal) var(--ease-out-expo);
}

a:hover::before {
  transform: scaleX(1);
}

:deep(.el-tag) {
  border-radius: var(--border-radius-sm);
  font-weight: 500;
}

/* 动画效果 */
.my-posts {
  animation: fadeIn var(--transition-normal) var(--ease-out-expo);
}

@media (max-width: 768px) {
  .my-posts {
    padding: var(--spacing-md);
  }

  .header h2 {
    font-size: 1.2rem;
  }

  .create-post-btn {
    padding: 8px 16px;
  }

  .action-button {
    padding: 6px 12px;
  }
}
</style> 