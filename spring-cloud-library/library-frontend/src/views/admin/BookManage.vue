<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>图书管理</h3>
      <el-button type="primary" @click="openAddDialog">添加图书</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索书名" clearable style="width: 300px" @keyup.enter="fetchBooks" />
      <el-button type="primary" @click="fetchBooks" style="margin-left: 12px">搜索</el-button>
    </div>

    <el-table :data="books" v-loading="loading" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="书名" min-width="180" />
      <el-table-column prop="author" label="作者" width="130" />
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column label="库存" width="80">
        <template #default="{ row }">{{ row.availableCopies }} / {{ row.totalCopies }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="fetchBooks" />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑图书' : '添加图书'" width="550px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="书名" required>
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input-number v-model="form.totalCopies" :min="1" :max="999" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入图书描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookList, addBook, updateBook, deleteBook } from '@/api/book'

const books = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref()

const form = reactive({
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  category: '',
  description: '',
  totalCopies: 1
})

async function fetchBooks() {
  loading.value = true
  try {
    const res: any = await getBookList({ page: page.value, size: size.value, keyword: keyword.value || undefined })
    books.value = res.data.records
    total.value = res.data.total
  } catch { } finally { loading.value = false }
}

function openAddDialog() {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { title: '', author: '', isbn: '', publisher: '', category: '', description: '', totalCopies: 1 })
  dialogVisible.value = true
}

function openEditDialog(row: any) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    title: row.title, author: row.author || '', isbn: row.isbn || '',
    publisher: row.publisher || '', category: row.category || '',
    description: row.description || '', totalCopies: row.totalCopies
  })
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.title.trim()) {
    ElMessage.warning('请输入书名')
    return
  }
  try {
    if (isEdit.value && editId.value) {
      await updateBook(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await addBook(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchBooks()
  } catch { }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗？', '确认删除', { type: 'warning' })
    await deleteBook(id)
    ElMessage.success('删除成功')
    fetchBooks()
  } catch { }
}

onMounted(fetchBooks)
</script>

<style scoped>
.admin-page {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-header h3 { font-size: 18px; color: #333; }
.search-bar { margin-top: 16px; display: flex; align-items: center; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
