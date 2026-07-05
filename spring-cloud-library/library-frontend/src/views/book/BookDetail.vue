<template>
  <div class="book-detail" v-loading="loading">
    <div class="page-header">
      <el-button @click="router.push('/books')" text>
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <div v-if="book" class="detail-content">
      <el-descriptions title="图书详情" border :column="2">
        <el-descriptions-item label="书名">{{ book.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ book.author }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ book.isbn || '-' }}</el-descriptions-item>
        <el-descriptions-item label="出版社">{{ book.publisher || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ book.category || '-' }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ book.availableCopies }} / {{ book.totalCopies }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ book.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>

      <div class="action-bar">
        <el-button type="primary" size="large" :disabled="book.availableCopies <= 0"
          :loading="borrowing" @click="handleBorrow">
          {{ book.availableCopies > 0 ? '立即借阅' : '暂无可借' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getBookById } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const book = ref<any>(null)
const loading = ref(false)
const borrowing = ref(false)

async function fetchBook() {
  loading.value = true
  try {
    const res: any = await getBookById(Number(route.params.id))
    book.value = res.data
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleBorrow() {
  borrowing.value = true
  try {
    const userId = userStore.getUserId()
    if (!userId) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    await borrowBook(userId, book.value.id, 30)
    ElMessage.success('借阅成功！请在30天内归还')
    fetchBook() // Refresh stock
  } catch {
    // handled by interceptor
  } finally {
    borrowing.value = false
  }
}

onMounted(fetchBook)
</script>

<style scoped>
.book-detail {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.detail-content {
  max-width: 900px;
}

.action-bar {
  margin-top: 24px;
}
</style>
