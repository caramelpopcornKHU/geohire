<template>
  <div class="page">
    <div class="container">
      <div class="dash-header slide-up">
        <div>
          <h1>기업 대시보드</h1>
          <p class="dash-sub">채용공고를 관리하세요</p>
        </div>
        <RouterLink to="/jobs/new" class="btn btn-primary">+ 새 공고 등록</RouterLink>
      </div>

      <div v-if="loading" class="loading-state">불러오는 중...</div>
      <div v-else-if="jobs.length === 0" class="empty-state card">
        <span class="empty-icon">📋</span>
        <p>등록된 채용공고가 없습니다.</p>
        <RouterLink to="/jobs/new" class="btn btn-primary" style="margin-top: 16px">첫 공고 등록하기</RouterLink>
      </div>
      <div v-else class="dash-jobs">
        <div v-for="job in jobs" :key="job.id" class="card dash-job fade-in">
          <div class="dash-job-main">
            <h3 class="dash-job-title">{{ job.title }}</h3>
            <div class="dash-job-meta">
              <span class="badge" :class="statusBadge(job.status)">{{ statusLabel(job.status) }}</span>
              <span>{{ typeLabel(job.employmentType) }}</span>
              <span v-if="job.location">📍 {{ job.location }}</span>
              <span v-if="job.deadline">마감: {{ job.deadline }}</span>
            </div>
            <div class="dash-job-skills">
              <span v-for="skill in job.skills" :key="skill.id" class="badge badge-primary">{{ skill.name }}</span>
            </div>
          </div>
          <div class="dash-job-actions">
            <RouterLink :to="{ name: 'job-applicants', params: { id: job.id } }" class="btn btn-sm btn-primary">👥 지원자</RouterLink>
            <RouterLink :to="{ name: 'job-edit', params: { id: job.id } }" class="btn btn-sm btn-secondary">수정</RouterLink>
            <button class="btn btn-sm btn-outline" @click="deleteJob(job.id)" style="color: var(--danger); border-color: var(--danger);">삭제</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/api'

const jobs = ref([])
const loading = ref(true)

onMounted(fetchJobs)

async function fetchJobs() {
  loading.value = true
  try {
    const { data } = await api.get('/jobs/my', { params: { size: 50 } })
    jobs.value = data.content
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function deleteJob(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await api.delete(`/jobs/${id}`)
    jobs.value = jobs.value.filter((j) => j.id !== id)
  } catch (e) {
    alert('삭제에 실패했습니다.')
  }
}

function statusLabel(s) {
  const map = { OPEN: '모집중', CLOSED: '마감', DRAFT: '임시저장' }
  return map[s] || s
}

function statusBadge(s) {
  const map = { OPEN: 'badge-success', CLOSED: 'badge-warning', DRAFT: 'badge-accent' }
  return map[s] || 'badge-primary'
}

function typeLabel(t) {
  const map = { FULL_TIME: '정규직', CONTRACT: '계약직', INTERN: '인턴', FREELANCE: '프리랜서' }
  return map[t] || t
}
</script>

<style scoped>
.dash-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.dash-header h1 {
  font-size: 1.75rem;
  font-weight: 800;
}

.dash-sub {
  color: var(--text-secondary);
  margin-top: 4px;
}

.dash-jobs {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dash-job {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.dash-job-title {
  font-weight: 700;
  font-size: 1.05rem;
  margin-bottom: 8px;
}

.dash-job-meta {
  display: flex;
  gap: 12px;
  font-size: 0.8rem;
  color: var(--text-secondary);
  align-items: center;
  margin-bottom: 10px;
}

.dash-job-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.dash-job-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
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

@media (max-width: 768px) {
  .dash-header { flex-direction: column; gap: 16px; }
  .dash-job { flex-direction: column; }
}
</style>
