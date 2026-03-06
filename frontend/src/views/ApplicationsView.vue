<template>
  <div class="page">
    <div class="container">
      <div class="app-container slide-up">
        <h1 class="app-title">지원 내역</h1>
        <p class="app-sub">내가 지원한 채용공고 현황을 확인하세요</p>

        <div v-if="loading" class="loading-state">불러오는 중...</div>
        <div v-else-if="applications.length === 0" class="empty-state card">
          <span class="empty-icon">📬</span>
          <p>아직 지원한 공고가 없습니다.</p>
          <RouterLink to="/jobs" class="btn btn-primary" style="margin-top: 16px">채용공고 둘러보기</RouterLink>
        </div>
        <div v-else class="app-list">
          <div v-for="app in applications" :key="app.id" class="card app-card fade-in">
            <div class="app-main">
              <RouterLink :to="{ name: 'job-detail', params: { id: app.jobPostingId } }" class="app-job-title">
                {{ app.jobTitle }}
              </RouterLink>
              <span class="app-company">{{ app.companyName }}</span>
              <span class="app-date">{{ formatDate(app.appliedAt) }} 지원</span>
            </div>
            <div class="app-status">
              <span class="badge" :class="statusBadge(app.status)">{{ statusLabel(app.status) }}</span>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button class="btn btn-sm btn-secondary" :disabled="currentPage === 0" @click="goPage(currentPage - 1)">← 이전</button>
          <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button class="btn btn-sm btn-secondary" :disabled="currentPage >= totalPages - 1" @click="goPage(currentPage + 1)">다음 →</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/api'

const applications = ref([])
const loading = ref(true)
const currentPage = ref(0)
const totalPages = ref(0)

onMounted(() => fetchApps())

async function fetchApps() {
  loading.value = true
  try {
    const { data } = await api.get('/applications/my', { params: { page: currentPage.value, size: 20 } })
    applications.value = data.content
    totalPages.value = data.totalPages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goPage(page) {
  currentPage.value = page
  fetchApps()
}

function statusLabel(s) {
  const map = { PENDING: '검토 대기', REVIEWED: '검토 중', ACCEPTED: '합격', REJECTED: '불합격' }
  return map[s] || s
}

function statusBadge(s) {
  const map = { PENDING: 'badge-warning', REVIEWED: 'badge-accent', ACCEPTED: 'badge-success', REJECTED: 'badge-danger' }
  return map[s] || 'badge-primary'
}

function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}
</script>

<style scoped>
.app-container {
  max-width: 800px;
  margin: 0 auto;
}

.app-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 4px;
}

.app-sub {
  color: var(--text-secondary);
  margin-bottom: 28px;
}

.app-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.app-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.app-job-title {
  font-weight: 700;
  font-size: 1rem;
  color: var(--text);
  text-decoration: none;
  transition: var(--transition);
}

.app-job-title:hover {
  color: var(--primary-light);
}

.app-company {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.app-date {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.page-info {
  font-size: 0.9rem;
  color: var(--text-secondary);
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

.badge-danger {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
}
</style>
