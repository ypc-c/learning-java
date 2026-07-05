<template>
  <div class="notice-list">
    <div class="page-header">
      <h3>通知公告</h3>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="公共公告" name="public" />
      <el-tab-pane label="我的消息" name="my" />
    </el-tabs>

    <div v-if="notices.length === 0 && !loading" class="empty-hint">
      <el-empty description="暂无通知" />
    </div>

    <div v-else class="notice-items">
      <el-card v-for="notice in notices" :key="notice.id" class="notice-card" shadow="hover"
        @click="showDetail(notice)">
        <div class="notice-item">
          <div class="notice-info">
            <div class="notice-title">
              <el-tag v-if="notice.type === 'OVERDUE_REMINDER'" type="danger" size="small">逾期提醒</el-tag>
              <el-tag v-else-if="notice.type === 'SYSTEM'" type="warning" size="small">系统通知</el-tag>
              <el-tag v-else type="primary" size="small">公告</el-tag>
              <span class="title-text">{{ notice.title }}</span>
              <span v-if="notice.isRead === 0" class="unread-dot"></span>
            </div>
            <div class="notice-time">{{ formatDate(notice.createTime) }}</div>
          </div>
          <div class="notice-preview">{{ truncate(notice.content, 100) }}</div>
        </div>
      </el-card>

      <div class="pagination" v-if="total > size">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total"
          layout="total, prev, pager, next" @current-change="fetchNotices" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="currentNotice?.title" width="600px">
      <div v-if="currentNotice" class="notice-detail">
        <p class="notice-meta">
          <el-tag v-if="currentNotice.type === 'OVERDUE_REMINDER'" type="danger" size="small">逾期提醒</el-tag>
          <el-tag v-else-if="currentNotice.type === 'SYSTEM'" type="warning" size="small">系统通知</el-tag>
          <el-tag v-else type="primary" size="small">公告</el-tag>
          {{ formatDate(currentNotice.createTime) }}
        </p>
        <div class="notice-content">{{ currentNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNoticeList, getMyNotices, getNoticeById } from '@/api/notice'

const notices = ref<any[]>([])
const loading = ref(false)
const activeTab = ref('public')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentNotice = ref<any>(null)

async function fetchNotices() {
  loading.value = true
  try {
    const api = activeTab.value === 'my' ? getMyNotices : getNoticeList
    const res: any = await api({ page: page.value, size: size.value })
    notices.value = res.data.records
    total.value = res.data.total
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function showDetail(notice: any) {
  try {
    const res: any = await getNoticeById(notice.id)
    currentNotice.value = res.data
    dialogVisible.value = true
    // Update unread status locally
    notice.isRead = 1
  } catch {
    // handled
  }
}

function handleTabChange() {
  page.value = 1
  fetchNotices()
}

function formatDate(date: string): string {
  if (!date) return '-'
  return date.replace('T', ' ').substring(0, 16)
}

function truncate(text: string, maxLen: number): string {
  if (!text) return ''
  return text.length > maxLen ? text.substring(0, maxLen) + '...' : text
}

onMounted(fetchNotices)
</script>

<style scoped>
.notice-list {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.page-header h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.empty-hint {
  margin-top: 40px;
}

.notice-items {
  margin-top: 8px;
}

.notice-card {
  margin-bottom: 12px;
  cursor: pointer;
}

.notice-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notice-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-weight: 500;
  font-size: 15px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
  display: inline-block;
}

.notice-time {
  color: #999;
  font-size: 13px;
}

.notice-preview {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.notice-detail {
  line-height: 1.8;
}

.notice-meta {
  color: #999;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-content {
  font-size: 15px;
  white-space: pre-wrap;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
