<template>
  <div class="my-borrows">
    <div class="page-header">
      <h3>我的借阅</h3>
    </div>

    <el-table :data="borrows" v-loading="loading" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="记录ID" width="80" />
      <el-table-column prop="bookTitle" label="书名" min-width="200">
        <template #default="{ row }">
          <el-link type="primary" @click="$router.push(`/books/${row.bookId}`)">{{ row.bookTitle }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="borrowTime" label="借阅时间" width="180">
        <template #default="{ row }">{{ formatDate(row.borrowTime) }}</template>
      </el-table-column>
      <el-table-column prop="dueTime" label="应还时间" width="180">
        <template #default="{ row }">{{ formatDate(row.dueTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'BORROWED'" type="warning">借阅中</el-tag>
          <el-tag v-else-if="row.status === 'OVERDUE'" type="danger">已逾期</el-tag>
          <el-tag v-else type="success">已归还</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.status === 'BORROWED' || row.status === 'OVERDUE'"
            type="primary" size="small" @click="handleReturn(row.id)">
            归还
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyBorrows, returnBook } from '@/api/borrow'

const borrows = ref<any[]>([])
const loading = ref(false)

async function fetchMyBorrows() {
  loading.value = true
  try {
    const res: any = await getMyBorrows()
    borrows.value = res.data
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function handleReturn(recordId: number) {
  try {
    await returnBook(recordId)
    ElMessage.success('归还成功')
    fetchMyBorrows()
  } catch {
    // handled
  }
}

function formatDate(date: string): string {
  if (!date) return '-'
  return date.replace('T', ' ').substring(0, 16)
}

onMounted(fetchMyBorrows)
</script>

<style scoped>
.my-borrows {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.page-header h3 {
  font-size: 18px;
  color: #333;
}
</style>
