<template>
  <div class="page">
    <div class="container">
      <!-- Search Bar -->
      <div class="search-section slide-up">
        <h1 class="search-title">채용공고 검색</h1>
        <div class="search-bar">
          <input
            v-model="filters.keyword"
            type="text"
            class="form-input search-input"
            placeholder="키워드, 기업명, 기술 검색..."
            @keyup.enter="fetchJobs"
          />
          <button class="btn btn-primary" @click="fetchJobs">검색</button>
        </div>

        <div class="filter-row">
          <select v-model="filters.employmentType" class="form-select filter-select" @change="fetchJobs">
            <option value="">고용형태 전체</option>
            <option value="FULL_TIME">정규직</option>
            <option value="CONTRACT">계약직</option>
            <option value="INTERN">인턴</option>
            <option value="FREELANCE">프리랜서</option>
          </select>
          <input
            v-model="filters.location"
            type="text"
            class="form-input filter-input"
            placeholder="지역"
            @keyup.enter="fetchJobs"
          />
        </div>
      </div>

      <!-- Job Cards -->
      <div v-if="loading" class="loading-state">불러오는 중...</div>
      <div v-else-if="jobs.length === 0" class="empty-state">
        <span class="empty-icon">📭</span>
        <p>검색 결과가 없습니다.</p>
      </div>
      <div v-else class="job-grid">
        <RouterLink
          v-for="job in jobs" :key="job.id"
          :to="{ name: 'job-detail', params: { id: job.id } }"
          class="job-card card fade-in"
        >
          <div class="job-card-header">
            <div class="job-company">
              <div class="company-avatar">{{ job.company?.companyName?.charAt(0) || '?' }}</div>
              <div>
                <span class="company-name">{{ job.company?.companyName }}</span>
                <span class="company-location">{{ job.company?.location || job.location }}</span>
              </div>
            </div>
            <span class="badge" :class="typeBadge(job.employmentType)">
              {{ typeLabel(job.employmentType) }}
            </span>
          </div>

          <h3 class="job-title">{{ job.title }}</h3>

          <div class="job-meta">
            <span v-if="job.location">📍 {{ job.location }}</span>
            <span v-if="job.salaryMin || job.salaryMax">
              💰 {{ formatSalary(job.salaryMin, job.salaryMax) }}
            </span>
          </div>

          <div class="job-skills">
            <span v-for="skill in job.skills" :key="skill.id" class="badge badge-primary">
              {{ skill.name }}
            </span>
          </div>

          <div class="job-deadline" v-if="job.deadline">
            마감: {{ job.deadline }}
          </div>
        </RouterLink>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          class="btn btn-sm btn-secondary"
          :disabled="currentPage === 0"
          @click="goPage(currentPage - 1)"
        >← 이전</button>
        <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
        <button
          class="btn btn-sm btn-secondary"
          :disabled="currentPage >= totalPages - 1"
          @click="goPage(currentPage + 1)"
        >다음 →</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import api from '@/api'

const route = useRoute()
const router = useRouter()

const jobs = ref([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)

const filters = reactive({
  keyword: route.query.keyword || '',
  location: '',
  employmentType: '',
})

onMounted(fetchJobs)

watch(() => route.query.keyword, (val) => {
  if (val) {
    filters.keyword = val
    fetchJobs()
  }
})

async function fetchJobs() {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: 12 }
    if (filters.keyword) params.keyword = filters.keyword
    if (filters.location) params.location = filters.location
    if (filters.employmentType) params.employmentType = filters.employmentType

    const { data } = await api.get('/jobs', { params })
    jobs.value = data.content
    totalPages.value = data.totalPages
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goPage(page) {
  currentPage.value = page
  fetchJobs()
}

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
</script>

<style scoped>
.search-section {
  margin-bottom: 40px;
}

.search-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
}

.filter-row {
  display: flex;
  gap: 12px;
}

.filter-select, .filter-input {
  max-width: 200px;
}

/* Job Cards */
.job-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.job-card {
  text-decoration: none;
  color: inherit;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.job-card:hover {
  transform: translateY(-3px);
  border-color: var(--primary);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.job-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.job-company {
  display: flex;
  align-items: center;
  gap: 10px;
}

.company-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.1rem;
}

.company-name {
  display: block;
  font-weight: 600;
  font-size: 0.85rem;
}

.company-location {
  display: block;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.job-title {
  font-size: 1.1rem;
  font-weight: 700;
  line-height: 1.4;
}

.job-meta {
  display: flex;
  gap: 16px;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.job-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.job-deadline {
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-top: auto;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 40px;
}

.page-info {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

/* States */
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
  .search-bar { flex-direction: column; }
  .filter-row { flex-direction: column; }
  .filter-select, .filter-input { max-width: 100%; }
  .job-grid { grid-template-columns: 1fr; }
}
</style>
