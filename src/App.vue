<!-- App.vue -->
<template>
  <el-config-provider>
    <el-container class="app-container">
      <Logo />
      <el-header class="app-header">
        <nav-header />
      </el-header>
      <el-container class="main-container">
        <el-aside width="240px" class="app-aside">
          <side-menu />
        </el-aside>
        <el-main class="app-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-config-provider>
</template>

<script setup>
import { ElConfigProvider, ElContainer, ElHeader, ElAside, ElMain } from 'element-plus'
import NavHeader from './components/NavHeader.vue'
import SideMenu from './components/SideMenu.vue'
import Logo from './components/Logo.vue'
</script>

<style>
@import './styles/variables.css';
@import './styles/animations.css';

/* 重置样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--bg-primary);
  color: var(--text-primary);
}

/* 容器样式 */
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  min-height: calc(100vh - 260px); /* 200px for logo + 60px for header */
}

/* 头部样式 */
.app-header {
  height: 60px !important;
  padding: 0;
  background-color: var(--primary-color);
  box-shadow: var(--shadow-sm);
  position: relative;
  z-index: 2;
  animation: scaleIn var(--transition-normal) var(--ease-out-expo);
}

/* 侧边栏样式 */
.app-aside {
  background-color: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal) var(--ease-out-expo);
  animation: fadeIn var(--transition-normal) var(--ease-out-expo);
}

/* 主内容区样式 */
.app-main {
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
}

/* Element Plus 主题覆盖 */
:root {
  --el-color-primary: var(--primary-color);
  --el-color-success: var(--success-color);
  --el-color-warning: var(--warning-color);
  --el-color-danger: var(--error-color);
  --el-color-info: var(--info-color);
}

/* 卡片样式优化 */
.el-card {
  border-radius: var(--border-radius-md);
  border: none;
  box-shadow: var(--shadow-sm) !important;
  transition: all var(--transition-normal) var(--ease-out-expo);
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg) !important;
}

/* 按钮样式优化 */
.el-button {
  border-radius: var(--border-radius-sm);
  font-weight: 500;
  position: relative;
  overflow: hidden;
  transition: all var(--transition-fast) var(--ease-out-expo);
}

.el-button::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.5);
  opacity: 0;
  border-radius: 100%;
  transform: scale(1);
  animation: ripple 1s cubic-bezier(0, 0, 0.2, 1);
}

.el-button:active::after {
  opacity: 0.32;
  transform: translate(-50%, -50%) scale(0);
  animation: ripple var(--transition-normal) var(--ease-out-expo);
}

.el-button--primary {
  background-color: var(--primary-color);
}

.el-button--primary:hover {
  background-color: var(--primary-dark);
}

/* 表格样式优化 */
.el-table {
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.el-table th {
  background-color: var(--bg-primary) !important;
  font-weight: 600;
}

/* 分页样式优化 */
.el-pagination {
  margin-top: var(--spacing-lg);
  justify-content: center;
}

/* 链接样式 */
a {
  position: relative;
  color: var(--primary-color);
  text-decoration: none;
  transition: all var(--transition-fast) var(--ease-out-expo);
}

a::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 2px;
  bottom: -2px;
  left: 0;
  background-color: var(--primary-color);
  transform: scaleX(0);
  transform-origin: bottom right;
  transition: transform var(--transition-normal) var(--ease-out-expo);
}

a:hover::after {
  transform: scaleX(1);
  transform-origin: bottom left;
}

/* 路由过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-normal) var(--ease-in-out),
              transform var(--transition-normal) var(--ease-out-expo);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateX(10px);
}

/* 加载动画 */
.el-loading-spinner {
  animation: rotateGrow var(--transition-normal) var(--ease-out-expo);
}

/* 表单控件动画 */
.el-input__inner,
.el-select .el-input__inner,
.el-textarea__inner {
  transition: all var(--transition-fast) var(--ease-out-expo);
}

.el-input__inner:focus,
.el-select .el-input__inner:focus,
.el-textarea__inner:focus {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(46, 125, 50, 0.1);
}

/* 下拉菜单动画 */
.el-select-dropdown {
  animation: scaleIn var(--transition-fast) var(--ease-out-expo);
}

/* 提示框动画 */
.el-message {
  animation: fadeIn var(--transition-fast) var(--ease-out-expo);
}

/* 对话框动画 */
.el-dialog__wrapper {
  animation: fadeIn var(--transition-fast) var(--ease-out-expo);
}

.el-dialog {
  animation: scaleIn var(--transition-normal) var(--ease-out-expo);
}

/* 表格动画 */
.el-table__row {
  transition: all var(--transition-fast) var(--ease-in-out);
}

.el-table__row:hover {
  transform: scale(1.01);
  background-color: rgba(46, 125, 50, 0.05) !important;
}
</style> 