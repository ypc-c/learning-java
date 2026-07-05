<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-left">
        <h2>📚 校园图书管理系统</h2>
      </div>
      <div class="header-right">
        <span class="user-info">欢迎，{{ userStore.userInfo?.username || '用户' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="sidebar">
        <el-menu :default-active="currentRoute" router :unique-opened="true">
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            <span>图书列表</span>
          </el-menu-item>
          <el-menu-item index="/borrows">
            <el-icon><Notebook /></el-icon>
            <span>我的借阅</span>
          </el-menu-item>
          <el-menu-item index="/notices">
            <el-icon><Bell /></el-icon>
            <span>通知公告</span>
          </el-menu-item>

          <template v-if="userStore.isAdmin()">
            <el-sub-menu index="admin">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>管理后台</span>
              </template>
              <el-menu-item index="/admin/users">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/books">
                <el-icon><Files /></el-icon>
                <span>图书管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/borrows">
                <el-icon><List /></el-icon>
                <span>借阅管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/notices">
                <el-icon><EditPen /></el-icon>
                <span>公告管理</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-aside>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentRoute = computed(() => route.path)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.header-left h2 {
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  font-size: 14px;
}

.sidebar {
  background: #fff;
  border-right: 1px solid #e8e8e8;
  overflow-y: auto;
}

.main-content {
  background: #f5f7fa;
  padding: 24px;
  min-height: calc(100vh - 60px);
}
</style>
