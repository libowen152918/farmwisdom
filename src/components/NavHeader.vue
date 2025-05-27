<!-- NavHeader.vue -->
<template>
  <el-menu mode="horizontal" :router="true" class="nav-menu">
    <el-menu-item index="/">
      <el-icon><House /></el-icon>
      农业知识论坛
    </el-menu-item>
    
    <div class="flex-grow" />
    
    <template v-if="!userStore.isAuthenticated">
      <el-menu-item index="/login">登录</el-menu-item>
      <el-menu-item index="/register">注册</el-menu-item>
    </template>
    
    <template v-else>
      <el-menu-item index="/forum/create">
        <el-icon><Edit /></el-icon>
        发布帖子
      </el-menu-item>
      
      <el-sub-menu index="user">
        <template #title>
          <el-icon><User /></el-icon>
          {{ userStore.user?.nickname || userStore.user?.username }}
        </template>
        
        <el-menu-item index="/profile">
          <el-icon><Setting /></el-icon>
          个人资料
        </el-menu-item>
        
        <el-menu-item v-if="userStore.isAdmin" index="/admin">
          <el-icon><Monitor /></el-icon>
          管理后台
        </el-menu-item>
        
        <el-menu-item @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-menu-item>
      </el-sub-menu>
    </template>
  </el-menu>
</template>

<script setup>
import { ElMenu, ElMenuItem, ElSubMenu } from 'element-plus'
import { House, Edit, User, Setting, Monitor, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-menu {
  height: 60px;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-primary);
}

.flex-grow {
  flex-grow: 1;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  height: 60px;
  line-height: 60px;
}

:deep(.el-menu-item.is-active) {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: var(--bg-hover);
}
</style> 