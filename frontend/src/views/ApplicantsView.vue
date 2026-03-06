<template>
  <div class="page">
    <div class="container">
      <div class="applicants-container slide-up">
        <RouterLink to="/dashboard" class="back-link">← 대시보드로 돌아가기</RouterLink>
        <h1 class="page-title">지원자 목록</h1>
        <p class="page-sub">{{ jobTitle || '채용공고' }}에 지원한 후보자들입니다</p>

        <div v-if="loading" class="loading-state">불러오는 중...</div>
        <div v-else-if="applicants.length === 0" class="empty-state card">
          <span class="empty-icon">👥</span>
          <p>아직 지원자가 없습니다.</p>
        </div>
        <div v-else class="applicant-list">
          <RouterLink
            v-for="app in applicants" :key="app.id"
            :to="{ name: 'application-detail', params: { id: app.id } }"
            class="card applicant-card fade-in"
          >
            <div class="applicant-main">
              <div class="applicant-header">
                <h3>{{ app.seekerName || `지원자 #${app.id}` }}</h3>
                <span class="badge" :class="statusBadge(app.status)">{{ statusLabel(app.status) }}</span>
              </div>
              <div class="applicant-meta">
                <span v-if="app.seekerEmail">📧 {{ app.seekerEmail }}</span>
                <span v-if="app.seekerPhone">📱 {{ app.seekerPhone }}</span>
                <span>{{ formatDate(app.appliedAt) }} 지원</span>
              </div>
              <p v-if="app.coverLetter" class="applicant-preview">{{ truncate(app.coverLetter, 100) }}</p>
            </div>
            <span class="view-arrow">→</span>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import api from '@/api'

const route = useRoute()
const applicants = ref([])
const jobTitle = ref('')
const loading = ref(true)

onMounted(async () => {
  try {
    const [appRes, jobRes] = await Promise.all([
      api.get(`/applications/jobs/${route.params.id}`, { params: { size: 50 } }),
      api.get(`/jobs/${route.params.id}`),
    ])
    applicants.value = appRes.data.content
    jobTitle.value = jobRes.data.title
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

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

function truncate(text, len) {
  if (!text) return ''
  return text.length > len ? text.slice(0, len) + '...' : text
}
</script>

<style scoped>
.applicants-container {
  max-width: 800px;
  margin: 0 auto;
}

.back-link {
  display: inline-block;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.85rem;
  margin-bottom: 16px;
  transition: var(--transition);
}

.back-link:hover { color: var(--primary-light); }

.page-title { font-size: 1.75rem; font-weight: 800; margin-bottom: 4px; }
.page-sub { color: var(--text-secondary); margin-bottom: 28px; }

.applicant-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.applicant-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  text-decoration: none;
  color: var(--text);
  cursor: pointer;
  transition: var(--transition);
}

.applicant-card:hover {
  border-color: var(--primary);
  transform: translateX(4px);
}

.applicant-main { flex: 1; }

.applicant-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.applicant-header h3 { font-weight: 700; font-size: 1rem; }

.applicant-meta {
  display: flex;
  gap: 16px;
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.applicant-preview {
  font-size: 0.85rem;
  color: var(--text-muted);
  line-height: 1.5;
}

.view-arrow {
  font-size: 1.2rem;
  color: var(--text-muted);
  flex-shrink: 0;
  transition: var(--transition);
}

.applicant-card:hover .view-arrow {
  color: var(--primary-light);
  transform: translateX(4px);
}

.badge-danger {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 0;
  color: var(--text-secondary);
}

.empty-icon { font-size: 3rem; display: block; margin-bottom: 12px; }

@media (max-width: 768px) {
  .applicant-meta { flex-direction: column; gap: 4px; }
}
</style>
