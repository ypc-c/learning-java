<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>公告管理</h3>
      <el-button type="primary" @click="openAddDialog">发布公告</el-button>
    </div>

    <el-table :data="notices" v-loading="loading" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.type === 'ANNOUNCEMENT'" type="primary">公告</el-tag>
          <el-tag v-else-if="row.type === 'SYSTEM'" type="warning">系统通知</el-tag>
          <el-tag v-else type="danger">逾期提醒</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="170">
        <template #default="{ row }">{{ row.createTime ? row.createTime.replace('T', ' ').substring(0, 16) : '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="fetchNotices" />
    </div>

    <el-dialog v-model="dialogVisible" title="发布公告" width="550px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="公告" value="ANNOUNCEMENT" />
            <el-option label="系统通知" value="SYSTEM" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" rows="6" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, createNotice, deleteNotice } from '@/api/notice'

const notices = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)

const form = reactive({ title: '', content: '', type: 'ANNOUNCEMENT' })

async function fetchNotices() {
  loading.value = true
  try {
    const res: any = await getNoticeList({ page: page.value, size: size.value })
    notices.value = res.data.records
    total.value = res.data.total
  } catch { } finally { loading.value = false }
}

function openAddDialog() {
  form.title = ''
  form.content = ''
  form.type = 'ANNOUNCEMENT'
  dialogVisible.value = true
}

async function handleCreate() {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  try {
    await createNotice(form)
    ElMessage.success('公告发布成功')
    dialogVisible.value = false
    fetchNotices()
  } catch { }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？', '确认', { type: 'warning' })
    await deleteNotice(id)
    ElMessage.success('删除成功')
    fetchNotices()
  } catch { }
}

onMounted(fetchNotices)
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
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
