<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>用户总数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.userCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>帖子总数</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.postCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>作物数量</span>
              <el-icon><Goods /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.cropCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>天气记录</span>
              <el-icon><Sunny /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.weatherCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速访问卡片 -->
    <el-row :gutter="20" class="quick-access">
      <el-col :span="6">
        <el-card shadow="hover" class="access-card" @click="navigateTo('/admin/users')">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="access-card" @click="navigateTo('/admin/posts')">
          <el-icon><Document /></el-icon>
          <span>帖子管理</span>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="access-card" @click="navigateTo('/admin/crops')">
          <el-icon><Goods /></el-icon>
          <span>作物管理</span>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="access-card" @click="navigateTo('/admin/weather')">
          <el-icon><Sunny /></el-icon>
          <span>天气管理</span>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card class="recent-activities">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
          <el-button type="primary" link @click="fetchRecentActivities">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :timestamp="activity.time"
          :type="activity.type"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Document, Goods, Sunny, Refresh } from '@element-plus/icons-vue'
import { getStats } from '@/api/admin'
import { ElMessage } from 'element-plus'

const router = useRouter()
const stats = ref({
  userCount: 0,
  postCount: 0,
  cropCount: 0,
  weatherCount: 0
})

const recentActivities = ref([])
let refreshTimer = null

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await getStats()
    // getStats现在直接返回data，所以直接赋值给stats.value
    if (response && typeof response === 'object' && response !== null) {
      stats.value = response
    } else {
      // 如果获取的数据格式不正确或为空，记录警告并保持默认值
      console.warn('获取的统计数据格式不正确或为空', response);
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取最近活动
const fetchRecentActivities = async () => {
  try {
    // TODO: 实现获取最近活动的API
    // 这里暂时使用模拟数据
    recentActivities.value = [
      {
        id: 1,
        content: '新用户 "张三" 注册',
        time: new Date().toLocaleString(),
        type: 'success'
      },
      {
        id: 2,
        content: '用户 "李四" 发布了新帖子',
        time: new Date().toLocaleString(),
        type: 'primary'
      },
      {
        id: 3,
        content: '管理员添加了新的作物信息',
        time: new Date().toLocaleString(),
        type: 'warning'
      }
    ]
  } catch (error) {
    console.error('获取最近活动失败:', error)
    ElMessage.error('获取最近活动失败')
  }
}

const navigateTo = (path) => {
  router.push(path)
}

// 设置定时刷新
const setupRefreshTimer = () => {
  // 每60秒刷新一次数据
  refreshTimer = setInterval(() => {
    fetchStats()
    fetchRecentActivities()
  }, 60000)
}

onMounted(() => {
  fetchStats()
  fetchRecentActivities()
  setupRefreshTimer()
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  margin-top: 10px;
}

.quick-access {
  margin-bottom: 20px;
}

.access-card {
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.access-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.access-card .el-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

.recent-activities {
  margin-top: 20px;
}

.recent-activities .el-timeline {
  padding: 20px;
}
</style> 