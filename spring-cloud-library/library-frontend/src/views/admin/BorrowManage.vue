<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>借阅管理</h3>
    </div>

    <el-table :data="borrows" v-loading="loading" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="记录ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="userName" label="用户名" width="120" />
      <el-table-column prop="bookTitle" label="书名" min-width="180" />
      <el-table-column prop="borrowTime" label="借阅时间" width="170">
        <template #default="{ row }">{{ row.borrowTime ? row.borrowTime.replace('T', ' ').substring(0, 16) : '-' }}</template>
      </el-table-column>
      <el-table-column prop="dueTime" label="应还时间" width="170">
        <template #default="{ row }">{{ row.dueTime ? row.dueTime.replace('T', ' ').substring(0, 16) : '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'BORROWED'" type="warning">借阅中</el-tag>
          <el-tag v-else-if="row.status === 'OVERDUE'" type="danger">已逾期</el-tag>
          <el-tag v-else type="success">已归还</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllBorrows } from '@/api/borrow'

const borrows = ref<any[]>([])
const loading = ref(false)

async function fetchAllBorrows() {
  loading.value = true
  try {
    const res: any = await getAllBorrows()
    borrows.value = res.data
  } catch { } finally { loading.value = false }
}

onMounted(fetchAllBorrows)
</script>

<style scoped>
.admin-page {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}
.page-header h3 { font-size: 18px; color: #333; }
</style>
