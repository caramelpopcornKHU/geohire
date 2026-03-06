<template>
  <div class="page">
    <div class="container">
      <div class="notif-container slide-up">
        <div class="notif-header">
          <div>
            <h1 class="notif-title">알림</h1>
            <p class="notif-sub">{{ unreadCount > 0 ? `읽지 않은 알림 ${unreadCount}개` : '새로운 알림이 없습니다' }}</p>
          </div>
          <button v-if="unreadCount > 0" class="btn btn-sm btn-secondary" @click="markAllRead">모두 읽음</button>
        </div>

        <div v-if="loading" class="loading-state">불러오는 중...</div>
        <div v-else-if="notifications.length === 0" class="empty-state card">
          <span class="empty-icon">🔔</span>
          <p>알림이 없습니다.</p>
        </div>
        <div v-else class="notif-list">
          <div
            v-for="n in notifications" :key="n.id"
            :class="['card', 'notif-card', 'fade-in', { 'unread': !n.read }]"
            @click="handleClick(n)"
          >
            <div class="notif-icon">{{ typeIcon(n.type) }}</div>
            <div class="notif-body">
              <h3 class="notif-card-title">{{ n.title }}</h3>
              <p class="notif-message">{{ n.message }}</p>
              <span class="notif-time">{{ formatTime(n.createdAt) }}</span>
            </div>
            <div v-if="!n.read" class="unread-dot"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const notifications = ref([])
const unreadCount = ref(0)
const loading = ref(true)

onMounted(async () => {
  try {
    const [nRes, cRes] = await Promise.all([
      api.get('/notifications', { params: { size: 50 } }),
      api.get('/notifications/unread-count'),
    ])
    notifications.value = nRes.data.content
    unreadCount.value = cRes.data.count
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

async function handleClick(n) {
  if (!n.read) {
    try {
      await api.put(`/notifications/${n.id}/read`)
      n.read = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (e) { /* ignore */ }
  }
  // 관련 페이지로 이동
  if (n.type === 'APPLICATION_RECEIVED' && n.referenceId) {
    router.push({ name: 'job-applicants', params: { id: n.referenceId } })
  } else if (n.type === 'APPLICATION_STATUS') {
    router.push({ name: 'applications' })
  }
}

async function markAllRead() {
  try {
    await api.put('/notifications/read-all')
    notifications.value.forEach((n) => (n.read = true))
    unreadCount.value = 0
  } catch (e) { /* ignore */ }
}

function typeIcon(type) {
  const map = {
    APPLICATION_RECEIVED: '📩',
    APPLICATION_STATUS: '📋',
    JOB_DEADLINE: '⏰',
  }
  return map[type] || '🔔'
}

function formatTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '방금 전'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}분 전`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}시간 전`
  return d.toLocaleDateString('ko-KR')
}
</script>

<style scoped>
.notif-container {
  max-width: 700px;
  margin: 0 auto;
}

.notif-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.notif-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 4px;
}

.notif-sub {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.notif-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notif-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  cursor: pointer;
  padding: 18px 20px;
  transition: var(--transition);
}

.notif-card.unread {
  border-color: var(--primary);
  background: rgba(79, 70, 229, 0.05);
}

.notif-card:hover {
  transform: translateX(4px);
}

.notif-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
  margin-top: 2px;
}

.notif-body {
  flex: 1;
}

.notif-card-title {
  font-size: 0.9rem;
  font-weight: 700;
  margin-bottom: 4px;
}

.notif-message {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-bottom: 6px;
  line-height: 1.5;
}

.notif-time {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary-light);
  flex-shrink: 0;
  margin-top: 8px;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 0;
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 12px;
}
</style>
