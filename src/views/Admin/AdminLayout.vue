<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/posts">
            <el-icon><Document /></el-icon>
            <span>帖子管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/crops">
            <el-icon><Crop /></el-icon>
            <span>作物管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/weather">
            <el-icon><Sunny /></el-icon>
            <span>天气管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-container>
        <el-header height="60px">
          <div class="header-content">
            <h2>农场智慧管理系统</h2>
            <div class="user-info">
              <el-dropdown>
                <span class="el-dropdown-link">
                  {{ username }}
                  <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>

        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/userStore'
import { DataLine, User, Crop, Sunny, ArrowDown, Document } from '@element-plus/icons-vue'
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const username = computed(() => userStore.username)
const activeMenu = computed(() => route.path)

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #409EFF;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style> 