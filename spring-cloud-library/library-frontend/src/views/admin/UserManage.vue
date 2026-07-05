<template>
  <div class="admin-page" v-loading="loading">
    <div class="page-header">
      <h3>用户管理</h3>
      <span class="total-hint">共 {{ users.length }} 个用户</span>
    </div>
    <el-table :data="users" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="realName" label="真实姓名" width="150" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'" size="small">
            {{ row.role === 'ADMIN' ? '管理员' : '学生' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
    </el-table>
    <el-empty v-if="!loading && users.length === 0" description="暂无用户数据" style="margin-top: 30px" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserList } from '@/api/user'

const users = ref<any[]>([])
const loading = ref(false)

async function fetchUsers() {
  loading.value = true
  try {
    const res: any = await getUserList()
    users.value = res.data || []
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-page {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-header h3 { font-size: 18px; color: #333; margin: 0; }
.total-hint { color: #999; font-size: 14px; }
</style>
