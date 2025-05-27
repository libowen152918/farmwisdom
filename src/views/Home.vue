<!-- Home.vue -->
<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <h2>欢迎来到农业知识论坛</h2>
            </div>
          </template>
          <div class="welcome-content">
            <p>这里是农业知识交流的平台，您可以：</p>
            <ul>
              <li>浏览和学习农业种植技术</li>
              <li>了解最新的病虫害防治方法</li>
              <li>获取实时的农产品市场行情</li>
              <li>与农业专家在线交流</li>
            </ul>
          </div>
        </el-card>

        <el-card class="latest-posts">
          <template #header>
            <div class="card-header">
              <h3>最新帖子</h3>
              <el-button type="primary" @click="$router.push('/forum')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="latestPosts" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="createTime" label="发布时间" width="180" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="quick-actions">
          <template #header>
            <div class="card-header">
              <h3>快捷操作</h3>
            </div>
          </template>
          <div class="action-buttons">
            <el-button type="primary" @click="$router.push('/forum/create')" :disabled="!userStore.isAuthenticated">
              发布帖子
            </el-button>
            <el-button type="success" @click="$router.push('/forum/tech')">
              种植技术
            </el-button>
            <el-button type="warning" @click="$router.push('/forum/disease')">
              病虫害防治
            </el-button>
            <el-button type="info" @click="$router.push('/forum/market')">
              市场行情
            </el-button>
          </div>
        </el-card>

        <el-card class="expert-online">
          <template #header>
            <div class="card-header">
              <h3>在线专家</h3>
            </div>
          </template>
          <div class="expert-list">
            <div v-for="expert in onlineExperts" :key="expert.id" class="expert-item">
              <el-avatar :size="40" :src="expert.avatar" />
              <span class="expert-name">{{ expert.name }}</span>
              <el-tag size="small" type="success">在线</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 模拟数据
const latestPosts = ref([
  {
    title: '水稻种植技术分享',
    author: '农业专家张三',
    createTime: '2024-03-15 10:30'
  },
  {
    title: '果树病虫害防治方法',
    author: '植保专家李四',
    createTime: '2024-03-15 09:15'
  },
  {
    title: '最新蔬菜市场行情分析',
    author: '市场分析师王五',
    createTime: '2024-03-15 08:45'
  }
])

const onlineExperts = ref([
  {
    id: 1,
    name: '张三',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=张三',
    specialty: '水稻种植'
  },
  {
    id: 2,
    name: '李四',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=李四',
    specialty: '植物保护'
  }
])
</script>

<style scoped>
.home {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  font-size: 16px;
  line-height: 1.6;
}

.latest-posts {
  margin-bottom: 20px;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.expert-online {
  margin-bottom: 20px;
}

.expert-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.expert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.expert-name {
  flex-grow: 1;
}

.el-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
</style> 