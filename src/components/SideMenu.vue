<!-- SideMenu.vue -->
<template>
  <div class="side-menu">
    <div class="menu-header">
      <div class="user-info" v-if="userStore.isAuthenticated">
        <el-avatar :size="50" class="user-avatar">{{ userStore.user?.nickname?.charAt(0) || 'U' }}</el-avatar>
        <div class="user-details">
          <div class="username">{{ userStore.user?.nickname || '用户' }}</div>
          <div class="user-role">{{ userStore.isAdmin ? '管理员' : '会员' }}</div>
        </div>
      </div>
      <el-button v-else type="text" @click="$router.push('/login')" class="login-btn">
        <el-icon><User /></el-icon>
        登录/注册
      </el-button>
    </div>

    <div class="category-section">
      <h3 class="section-title">
        <span class="title-icon">🌾</span>
        分类导航
      </h3>
      <div class="category-list">
        <router-link 
          to="/forum" 
          class="category-item"
          :class="{ active: $route.path === '/forum' }"
        >
          <span class="category-icon">📑</span>
          全部帖子
        </router-link>
        
        <router-link 
          v-for="category in categories"
          :key="category.id"
          :to="getCategoryPath(category.id)"
          class="category-item"
          :class="{ active: isCategoryActive(category.id) }"
        >
          <span class="category-icon">{{ getCategoryIcon(category.id) }}</span>
          {{ category.name }}
        </router-link>
      </div>
    </div>

    <div class="quick-actions">
      <h3 class="section-title">
        <span class="title-icon">⚡</span>
        快捷操作
      </h3>
      <div class="action-buttons">
        <el-button 
          type="success" 
          class="action-button"
          @click="$router.push('/forum/create')"
          v-if="userStore.isAuthenticated"
        >
          <el-icon><Edit /></el-icon>
          发布帖子
        </el-button>
        <el-button 
          type="primary" 
          class="action-button"
          @click="$router.push('/forum/my')"
          v-if="userStore.isAuthenticated"
        >
          <el-icon><Document /></el-icon>
          我的帖子
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  User,
  Edit,
  Document
} from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const route = useRoute()
const userStore = useUserStore()
const categories = ref([])

// 分类路径映射
const categoryPathMap = {
  1: 'tech',
  2: 'disease',
  3: 'market',
  4: 'experience',
  5: 'policy',
  6: 'tools'
}

// 分类图标映射
const categoryIconMap = {
  1: '🌱', // 种植技术
  2: '🔬', // 病虫害防治
  3: '📊', // 市场行情
  4: '📝', // 经验分享
  5: '📜', // 农业政策
  6: '🔧'  // 设备工具
}

// 获取分类路径
const getCategoryPath = (categoryId) => {
  const path = categoryPathMap[categoryId]
  return path ? `/forum/${path}` : '/forum'
}

// 获取分类图标
const getCategoryIcon = (categoryId) => {
  return categoryIconMap[categoryId] || '📑'
}

// 判断分类是否激活
const isCategoryActive = (categoryId) => {
  const path = categoryPathMap[categoryId]
  return path ? route.path === `/forum/${path}` : false
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.side-menu {
  height: 100%;
  background: linear-gradient(to bottom, var(--bg-secondary) 0%, rgba(241, 248, 233, 0.8) 100%);
  border-right: 1px solid rgba(46, 125, 50, 0.1);
  padding: 20px;
}

.menu-header {
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(46, 125, 50, 0.1);
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(46, 125, 50, 0.2);
  transition: transform var(--transition-fast) var(--ease-out-expo);
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-details {
  flex: 1;
}

.username {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.user-role {
  font-size: 0.85rem;
  color: var(--primary-color);
  opacity: 0.8;
}

.login-btn {
  width: 100%;
  height: 40px;
  color: var(--primary-color);
  border: 1px dashed var(--primary-color);
  transition: all var(--transition-fast) var(--ease-out-expo);
}

.login-btn:hover {
  background: rgba(46, 125, 50, 0.05);
  border-style: solid;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  color: var(--primary-dark);
  margin-bottom: 16px;
  padding-left: 8px;
  position: relative;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background: var(--primary-color);
  border-radius: 2px;
}

.title-icon {
  font-size: 1.2rem;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  color: var(--text-primary);
  text-decoration: none;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast) var(--ease-out-expo);
  position: relative;
  overflow: hidden;
}

.category-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), transparent);
  opacity: 0;
  transition: opacity var(--transition-fast) var(--ease-out-expo);
}

.category-item:hover {
  background: rgba(46, 125, 50, 0.05);
  transform: translateX(5px);
}

.category-item.active {
  background: rgba(46, 125, 50, 0.1);
  color: var(--primary-color);
  font-weight: 500;
}

.category-item.active::before {
  opacity: 0.1;
}

.category-icon {
  font-size: 1.2rem;
  transition: transform var(--transition-fast) var(--ease-out-expo);
}

.category-item:hover .category-icon {
  transform: scale(1.2);
}

.quick-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(46, 125, 50, 0.1);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 40px;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast) var(--ease-out-expo);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.2);
}

/* 添加滚动条样式 */
.side-menu {
  scrollbar-width: thin;
  scrollbar-color: var(--primary-light) transparent;
}

.side-menu::-webkit-scrollbar {
  width: 6px;
}

.side-menu::-webkit-scrollbar-track {
  background: transparent;
}

.side-menu::-webkit-scrollbar-thumb {
  background-color: var(--primary-light);
  border-radius: 3px;
}

/* 动画效果 */
.side-menu {
  animation: slideIn var(--transition-normal) var(--ease-out-expo);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .side-menu {
    padding: 15px;
  }

  .category-item {
    padding: 10px 12px;
  }

  .action-button {
    height: 36px;
  }
}
</style> 