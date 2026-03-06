<template>
  <div class="page">
    <div class="container">
      <div v-if="loading" class="loading-state">불러오는 중...</div>
      <div v-else-if="!job" class="empty-state">채용공고를 찾을 수 없습니다.</div>
      <div v-else class="job-detail slide-up">
        <div class="detail-main">
          <div class="detail-header">
            <div class="company-info">
              <div class="company-avatar-lg">{{ job.company?.companyName?.charAt(0) }}</div>
              <div>
                <h3 class="company-name">{{ job.company?.companyName }}</h3>
                <span class="company-meta">{{ job.company?.industry }} · {{ job.company?.location }}</span>
              </div>
            </div>
            <span class="badge" :class="typeBadge(job.employmentType)">{{ typeLabel(job.employmentType) }}</span>
          </div>

          <h1 class="detail-title">{{ job.title }}</h1>

          <div class="detail-info-row">
            <div v-if="job.location" class="info-item">📍 <strong>근무지</strong> {{ job.location }}</div>
            <div v-if="job.salaryMin || job.salaryMax" class="info-item">💰 <strong>연봉</strong> {{ formatSalary(job.salaryMin, job.salaryMax) }}</div>
            <div v-if="job.deadline" class="info-item">📅 <strong>마감</strong> {{ job.deadline }}</div>
          </div>

          <div v-if="job.skills?.length" class="detail-skills">
            <h3>요구 기술</h3>
            <div class="skill-list">
              <span v-for="skill in job.skills" :key="skill.id" class="badge badge-primary">{{ skill.name }}</span>
            </div>
          </div>

          <div class="detail-description">
            <h3>상세 내용</h3>
            <div class="description-content" v-html="formatDescription(job.description)"></div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="detail-sidebar">
          <!-- 이미 지원한 경우 -->
          <div v-if="alreadyApplied" class="card sidebar-card applied-card">
            <div class="applied-icon">✅</div>
            <h3>지원 완료</h3>
            <p>이미 이 공고에 지원하셨습니다.</p>
            <RouterLink :to="{ name: 'applications' }" class="btn btn-sm btn-secondary" style="width: 100%; text-align: center;">
              지원 내역 보기
            </RouterLink>
          </div>

          <!-- 지원하기 -->
          <div v-else class="card sidebar-card">
            <h3>지원하기</h3>
            <p>이 포지션에 관심이 있으신가요?</p>
            <template v-if="auth.isLoggedIn && auth.isSeeker">
              <RouterLink :to="{ name: 'apply', params: { id: job.id } }" class="btn btn-primary btn-lg" style="width: 100%; text-align: center;">
                📝 지원서 작성하기
              </RouterLink>
            </template>
            <template v-else-if="auth.isLoggedIn && auth.isCompany">
              <p class="apply-note">기업 계정으로는 지원할 수 없습니다.</p>
            </template>
            <template v-else>
              <RouterLink to="/login" class="btn btn-primary btn-lg" style="width: 100%; text-align: center;">
                로그인 후 지원
              </RouterLink>
              <p class="apply-note">지원하려면 구직자 계정으로 로그인하세요.</p>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/api'

const route = useRoute()
const auth = useAuthStore()

const job = ref(null)
const loading = ref(true)
const alreadyApplied = ref(false)

onMounted(async () => {
  try {
    const { data } = await api.get(`/jobs/${route.params.id}`)
    job.value = data

    if (auth.isLoggedIn && auth.isSeeker) {
      try {
        const { data: applied } = await api.get(`/applications/check/${route.params.id}`)
        alreadyApplied.value = applied
      } catch (e) { /* ignore */ }
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

function typeLabel(type) {
  const map = { FULL_TIME: '정규직', CONTRACT: '계약직', INTERN: '인턴', FREELANCE: '프리랜서' }
  return map[type] || type
}

function typeBadge(type) {
  const map = { FULL_TIME: 'badge-success', CONTRACT: 'badge-accent', INTERN: 'badge-warning', FREELANCE: 'badge-primary' }
  return map[type] || 'badge-primary'
}

function formatSalary(min, max) {
  const fmt = (v) => (v / 10000).toFixed(0) + '만원'
  if (min && max) return `${fmt(min)} ~ ${fmt(max)}`
  if (min) return `${fmt(min)} 이상`
  if (max) return `${fmt(max)} 이하`
  return ''
}

function formatDescription(text) {
  if (!text) return ''
  return text.replace(/\n/g, '<br />')
}
</script>

<style scoped>
.job-detail {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 32px;
  align-items: start;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.company-avatar-lg {
  width: 56px;
  height: 56px;
  border-radius: var(--radius);
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 1.4rem;
}

.company-name { font-weight: 700; font-size: 1rem; }
.company-meta { font-size: 0.85rem; color: var(--text-secondary); }

.detail-title {
  font-size: 2rem;
  font-weight: 900;
  margin-bottom: 24px;
  line-height: 1.3;
}

.detail-info-row { display: flex; gap: 24px; margin-bottom: 32px; flex-wrap: wrap; }
.info-item { font-size: 0.9rem; color: var(--text-secondary); }
.info-item strong { color: var(--text); margin: 0 4px; }

.detail-skills, .detail-description { margin-bottom: 32px; }
.detail-skills h3, .detail-description h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 14px; }
.skill-list { display: flex; flex-wrap: wrap; gap: 8px; }
.description-content { color: var(--text-secondary); line-height: 1.8; font-size: 0.95rem; }

/* Sidebar */
.sidebar-card { position: sticky; top: 88px; }
.sidebar-card h3 { font-weight: 700; margin-bottom: 8px; }
.sidebar-card p { color: var(--text-secondary); font-size: 0.85rem; margin-bottom: 20px; }

.applied-card {
  text-align: center;
  border-color: var(--success);
  background: rgba(16, 185, 129, 0.05);
}
.applied-icon { font-size: 2.5rem; margin-bottom: 8px; }
.applied-card h3 { color: var(--success); }

.apply-note {
  font-size: 0.8rem;
  color: var(--text-muted);
  text-align: center;
  margin-top: 12px;
  margin-bottom: 0;
}

.loading-state, .empty-state { text-align: center; padding: 60px 0; color: var(--text-secondary); }

@media (max-width: 768px) {
  .job-detail { grid-template-columns: 1fr; }
  .detail-title { font-size: 1.5rem; }
  .detail-info-row { flex-direction: column; gap: 8px; }
}
</style>
