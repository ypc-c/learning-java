<template>
  <div class="book-list">
    <div class="page-header">
      <h3>图书列表</h3>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索书名/作者/ISBN" clearable style="width: 300px"
        @keyup.enter="handleSearch" />
      <el-select v-model="category" placeholder="图书分类" clearable style="width: 180px; margin-left: 12px">
        <el-option label="计算机科学" value="Computer Science" />
        <el-option label="软件工程" value="Software Engineering" />
        <el-option label="人工智能" value="Artificial Intelligence" />
      </el-select>
      <el-button type="primary" @click="handleSearch" style="margin-left: 12px">搜索</el-button>
    </div>

    <el-table :data="books" v-loading="loading" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="书名" min-width="200">
        <template #default="{ row }">
          <el-link type="primary" @click="goDetail(row.id)">{{ row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="author" label="作者" width="150" />
      <el-table-column prop="publisher" label="出版社" width="180" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="isbn" label="ISBN" width="160" />
      <el-table-column label="库存" width="100">
        <template #default="{ row }">{{ row.availableCopies }} / {{ row.totalCopies }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="goDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total, prev, pager, next"
        @current-change="fetchBooks" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBookList } from '@/api/book'

const router = useRouter()
const books = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const category = ref('')

async function fetchBooks() {
  loading.value = true
  try {
    const res: any = await getBookList({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined,
      category: category.value || undefined
    })
    books.value = res.data.records
    total.value = res.data.total
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchBooks()
}

function goDetail(id: number) {
  router.push(`/books/${id}`)
}

onMounted(fetchBooks)
</script>

<style scoped>
.book-list {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.page-header h3 {
  font-size: 18px;
  color: #333;
}

.search-bar {
  margin-top: 16px;
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
