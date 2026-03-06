<template>
  <div class="page">
    <div class="container">
      <div v-if="loading" class="loading-state">불러오는 중...</div>
      <div v-else-if="!job" class="empty-state">공고를 찾을 수 없습니다.</div>
      <div v-else class="apply-container slide-up">
        <RouterLink :to="{ name: 'job-detail', params: { id: job.id } }" class="back-link">← 공고로 돌아가기</RouterLink>

        <!-- 공고 요약 -->
        <div class="card job-summary">
          <div class="summary-left">
            <div class="company-avatar">{{ job.company?.companyName?.charAt(0) }}</div>
            <div>
              <h2 class="summary-title">{{ job.title }}</h2>
              <p class="summary-company">{{ job.company?.companyName }} · {{ job.location }}</p>
            </div>
          </div>
          <span class="badge" :class="typeBadge(job.employmentType)">{{ typeLabel(job.employmentType) }}</span>
        </div>

        <!-- 지원서 폼 -->
        <form @submit.prevent="handleSubmit" class="apply-form">
          <h1 class="form-title">📝 지원서 작성</h1>
          <p class="form-sub">정성스럽게 작성한 지원서가 좋은 결과로 이어집니다.</p>

          <div v-if="applyError" class="error-msg">{{ applyError }}</div>

          <!-- 1. 지원 동기 -->
          <div class="card form-section">
            <div class="section-header">
              <span class="section-num">01</span>
              <div>
                <h3>지원 동기</h3>
                <p class="section-desc">이 포지션에 관심을 갖게 된 이유와 입사 후 기여하고 싶은 점을 알려주세요.</p>
              </div>
            </div>
            <div class="form-group">
              <textarea
                v-model="form.coverLetter"
                class="form-textarea"
                rows="6"
                placeholder="왜 이 회사, 이 포지션에 지원하게 되었는지 자유롭게 작성해주세요..."
              ></textarea>
            </div>
          </div>

          <!-- 2. 관련 경험 -->
          <div class="card form-section">
            <div class="section-header">
              <span class="section-num">02</span>
              <div>
                <h3>관련 경험 및 역량</h3>
                <p class="section-desc">본 공고와 관련된 실무 경험, 프로젝트, 핵심 역량을 구체적으로 작성해주세요.</p>
              </div>
            </div>
            <div class="form-group">
              <textarea
                v-model="form.experience"
                class="form-textarea"
                rows="8"
                placeholder="• 수행 프로젝트, 사용 기술, 성과 등을 구체적으로 기술해주세요
• 예: '000 프로젝트에서 QGIS + PostGIS를 활용한 공간분석 수행 (6개월)'
• 예: 'Python GeoPandas로 5만건 이상의 공간 데이터 ETL 파이프라인 구축'"
              ></textarea>
            </div>
          </div>

          <!-- 3. 포트폴리오 / 이력서 -->
          <div class="card form-section">
            <div class="section-header">
              <span class="section-num">03</span>
              <div>
                <h3>포트폴리오 & 이력서</h3>
                <p class="section-desc">작업물이나 이력서를 확인할 수 있는 URL을 첨부해주세요.</p>
              </div>
            </div>
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">이력서 URL</label>
                <input v-model="form.resumeUrl" type="url" class="form-input" placeholder="https://drive.google.com/..." />
              </div>
              <div class="form-group">
                <label class="form-label">포트폴리오 URL</label>
                <input v-model="form.portfolioUrl" type="url" class="form-input" placeholder="https://github.com/..." />
              </div>
            </div>
          </div>

          <!-- 4. 근무 조건 -->
          <div class="card form-section">
            <div class="section-header">
              <span class="section-num">04</span>
              <div>
                <h3>근무 조건</h3>
                <p class="section-desc">희망 연봉과 입사 가능일을 알려주세요.</p>
              </div>
            </div>
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">희망 연봉 (만원)</label>
                <input v-model.number="form.expectedSalary" type="number" class="form-input" placeholder="4000" />
              </div>
              <div class="form-group">
                <label class="form-label">입사 가능일</label>
                <input v-model="form.availableDate" type="date" class="form-input" />
              </div>
            </div>
          </div>

          <!-- 5. 추가 사항 -->
          <div class="card form-section">
            <div class="section-header">
              <span class="section-num">05</span>
              <div>
                <h3>추가 사항</h3>
                <p class="section-desc">기타 전달하고 싶은 내용이 있다면 자유롭게 작성해주세요.</p>
              </div>
            </div>
            <div class="form-group">
              <textarea
                v-model="form.additionalNote"
                class="form-textarea"
                rows="4"
                placeholder="예: 병역면제, 원격 근무 선호, 취업비자 필요 여부 등"
              ></textarea>
            </div>
          </div>

          <!-- 제출 -->
          <div class="submit-area">
            <button type="submit" class="btn btn-primary btn-lg submit-btn" :disabled="submitting">
              {{ submitting ? '제출 중...' : '🚀 지원서 제출하기' }}
            </button>
            <p class="submit-note">제출 후 수정이 불가합니다. 내용을 다시 한번 확인해주세요.</p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import api from '@/api'

const route = useRoute()
const router = useRouter()
const jobId = route.params.id

const job = ref(null)
const loading = ref(true)
const submitting = ref(false)
const applyError = ref('')

const form = reactive({
  coverLetter: '',
  experience: '',
  resumeUrl: '',
  portfolioUrl: '',
  expectedSalary: null,
  availableDate: '',
  additionalNote: '',
})

onMounted(async () => {
  try {
    const { data } = await api.get(`/jobs/${jobId}`)
    job.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

async function handleSubmit() {
  applyError.value = ''
  submitting.value = true
  try {
    const payload = {
      ...form,
      expectedSalary: form.expectedSalary || null,
      availableDate: form.availableDate || null,
    }
    await api.post(`/applications/jobs/${jobId}`, payload)
    router.push({ name: 'applications' })
  } catch (e) {
    applyError.value = e.response?.data?.message || '지원에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}

function typeLabel(t) {
  const map = { FULL_TIME: '정규직', CONTRACT: '계약직', INTERN: '인턴', FREELANCE: '프리랜서' }
  return map[t] || t
}

function typeBadge(t) {
  const map = { FULL_TIME: 'badge-success', CONTRACT: 'badge-accent', INTERN: 'badge-warning', FREELANCE: 'badge-primary' }
  return map[t] || 'badge-primary'
}
</script>

<style scoped>
.apply-container {
  max-width: 780px;
  margin: 0 auto;
}

.back-link {
  display: inline-block;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.85rem;
  margin-bottom: 20px;
  transition: var(--transition);
}

.back-link:hover {
  color: var(--primary-light);
}

/* 요약 카드 */
.job-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 20px 24px;
}

.summary-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.company-avatar {
  width: 48px;
  height: 48px;
  border-radius: var(--radius);
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 1.2rem;
}

.summary-title {
  font-weight: 700;
  font-size: 1.1rem;
  margin-bottom: 2px;
}

.summary-company {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

/* 폼 */
.form-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 4px;
}

.form-sub {
  color: var(--text-secondary);
  margin-bottom: 28px;
}

.form-section {
  padding: 28px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.section-num {
  min-width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 0.85rem;
  flex-shrink: 0;
}

.section-header h3 {
  font-weight: 700;
  font-size: 1rem;
  margin-bottom: 4px;
}

.section-desc {
  font-size: 0.8rem;
  color: var(--text-muted);
  line-height: 1.5;
}

/* 제출 */
.submit-area {
  text-align: center;
  padding: 32px 0 16px;
}

.submit-btn {
  min-width: 280px;
  font-size: 1rem;
  padding: 14px 32px;
}

.submit-note {
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-top: 12px;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 0;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .job-summary { flex-direction: column; gap: 12px; align-items: flex-start; }
}
</style>
