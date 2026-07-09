<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'

const menuItems = [
  { path: '/', label: '首页', icon: 'House' },
  { path: '/kits', label: '教具管理', icon: 'Package' },
  { path: '/staff', label: '教职工管理', icon: 'User' },
  { path: '/change-logs', label: '变更日志', icon: 'Document' },
  { path: '/monthly-summary', label: '月度汇总', icon: 'Calendar' },
  { path: '/staff-kits', label: '教具查询', icon: 'Search' }
]
</script>

<template>
  <el-layout class="app-container">
    <el-layout>
      <div class="header">
        <div class="logo">
          <span class="logo-icon">🔬</span>
          <span class="logo-text">研学基地科普教具管理系统</span>
        </div>
      </div>
      <div class="sidebar">
        <el-menu :default-active="$route.path" mode="vertical">
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <RouterLink :to="item.path">
              <span>{{ item.label }}</span>
            </RouterLink>
          </el-menu-item>
        </el-menu>
      </div>
      <el-layout class="main-content">
        <RouterView />
      </el-layout>
    </el-layout>
  </el-layout>
</template>

<style>
.app-container {
  height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
}

.sidebar {
  width: 200px;
  background: #2d3748;
}

.sidebar :deep(.el-menu) {
  background: transparent;
  border-right: none;
}

.sidebar :deep(.el-menu-item) {
  color: #a0aec0;
  margin: 4px 12px;
  border-radius: 8px;
}

.sidebar :deep(.el-menu-item:hover) {
  background: #4a5568;
  color: white;
}

.sidebar :deep(.el-menu-item.is-active) {
  background: #667eea;
  color: white;
}

.main-content {
  padding: 24px;
  overflow: auto;
}
</style>
