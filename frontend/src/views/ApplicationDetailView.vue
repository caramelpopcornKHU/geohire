<template>
  <div class="page">
    <div class="container">
      <div v-if="loading" class="loading-state">불러오는 중...</div>
      <div v-else-if="!app" class="empty-state">지원서를 찾을 수 없습니다.</div>
      <div v-else class="detail-container slide-up">
        <button class="back-link" @click="$router.back()">← 뒤로가기</button>

        <!-- 상단 요약 -->
        <div class="card summary-card">
          <div class="summary-top">
            <div>
              <h1 class="detail-title">{{ app.jobTitle }}</h1>
              <p class="detail-company">{{ app.companyName }}</p>
            </div>
            <span class="badge" :class="statusBadge(app.status)">{{ statusLabel(app.status) }}</span>
          </div>
          <div class="summary-meta">
            <span v-if="app.seekerName">👤 {{ app.seekerName }}</span>
            <span v-if="app.seekerEmail">📧 {{ app.seekerEmail }}</span>
            <span v-if="app.seekerPhone">📱 {{ app.seekerPhone }}</span>
            <span v-if="app.appliedAt">📅 {{ formatDate(app.appliedAt) }} 지원</span>
          </div>
        </div>

        <!-- 지원 동기 -->
        <div v-if="app.coverLetter" class="card section-card">
          <div class="section-header">
            <span class="section-num">01</span>
            <h3>지원 동기</h3>
          </div>
          <p class="section-body" v-html="nl2br(app.coverLetter)"></p>
        </div>

        <!-- 관련 경험 -->
        <div v-if="app.experience" class="card section-card">
          <div class="section-header">
            <span class="section-num">02</span>
            <h3>관련 경험 및 역량</h3>
          </div>
          <p class="section-body" v-html="nl2br(app.experience)"></p>
        </div>

        <!-- 포트폴리오 & 이력서 -->
        <div v-if="app.resumeUrl || app.portfolioUrl" class="card section-card">
          <div class="section-header">
            <span class="section-num">03</span>
            <h3>포트폴리오 & 이력서</h3>
          </div>
          <div class="link-row">
            <a v-if="app.resumeUrl" :href="app.resumeUrl" target="_blank" class="link-item">
              <span class="link-icon">📄</span>
              <div>
                <strong>이력서</strong>
                <span class="link-url">{{ app.resumeUrl }}</span>
              </div>
            </a>
            <a v-if="app.portfolioUrl" :href="app.portfolioUrl" target="_blank" class="link-item">
              <span class="link-icon">🔗</span>
              <div>
                <strong>포트폴리오</strong>
                <span class="link-url">{{ app.portfolioUrl }}</span>
              </div>
            </a>
          </div>
        </div>

        <!-- 근무 조건 -->
        <div v-if="app.expectedSalary || app.availableDate" class="card section-card">
          <div class="section-header">
            <span class="section-num">04</span>
            <h3>근무 조건</h3>
          </div>
          <div class="condition-row">
            <div v-if="app.expectedSalary" class="condition-item">
              <span class="condition-label">💰 희망 연봉</span>
              <span class="condition-value">{{ app.expectedSalary.toLocaleString() }}만원</span>
            </div>
            <div v-if="app.availableDate" class="condition-item">
              <span class="condition-label">📅 입사 가능일</span>
              <span class="condition-value">{{ app.availableDate }}</span>
            </div>
          </div>
        </div>

        <!-- 추가 사항 -->
        <div v-if="app.additionalNote" class="card section-card">
          <div class="section-header">
            <span class="section-num">05</span>
            <h3>추가 사항</h3>
          </div>
          <p class="section-body" v-html="nl2br(app.additionalNote)"></p>
        </div>

        <!-- 상태 변경 (기업만) -->
        <div v-if="isCompany" class="card section-card status-card">
          <h3>지원 상태 관리</h3>
          <div class="status-actions">
            <select :value="app.status" class="form-select" @change="updateStatus($event.target.value)">
              <option value="PENDING">검토 대기</option>
              <option value="REVIEWED">검토 중</option>
              <option value="ACCEPTED">합격</option>
              <option value="REJECTED">불합격</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/api'

const route = useRoute()
const auth = useAuthStore()
const app = ref(null)
const loading = ref(true)
const isCompany = computed(() => auth.isLoggedIn && auth.isCompany)

onMounted(async () => {
  try {
    const { data } = await api.get(`/applications/${route.params.id}`)
    app.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

async function updateStatus(status) {
  try {
    const { data } = await api.put(`/applications/${route.params.id}/status?status=${status}`)
    app.value = data
  } catch (e) {
    alert('상태 변경에 실패했습니다.')
  }
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
  return new Date(dt).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
}

function nl2br(text) {
  if (!text) return ''
  return text.replace(/\n/g, '<br />')
}
</script>

<style scoped>
.detail-container {
  max-width: 780px;
  margin: 0 auto;
}

.back-link {
  display: inline-block;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.85rem;
  margin-bottom: 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
  padding: 0;
  transition: var(--transition);
}

.back-link:hover { color: var(--primary-light); }

/* 상단 요약 */
.summary-card { padding: 24px 28px; margin-bottom: 16px; }

.summary-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-title { font-size: 1.4rem; font-weight: 800; margin-bottom: 4px; }
.detail-company { color: var(--text-secondary); font-size: 0.9rem; }

.summary-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 0.85rem;
  color: var(--text-secondary);
}

/* 섹션 카드 */
.section-card { padding: 24px 28px; margin-bottom: 12px; }

.section-header {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 16px;
}

.section-num {
  min-width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 0.8rem;
  flex-shrink: 0;
}

.section-header h3 { font-weight: 700; font-size: 1rem; }

.section-body {
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.8;
}

/* 링크 */
.link-row { display: flex; flex-direction: column; gap: 10px; }

.link-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--radius-sm);
  background: var(--bg);
  border: 1px solid var(--border);
  text-decoration: none;
  color: var(--text);
  transition: var(--transition);
}

.link-item:hover {
  border-color: var(--primary);
  transform: translateX(4px);
}

.link-icon { font-size: 1.4rem; }

.link-item strong { font-size: 0.85rem; display: block; margin-bottom: 2px; }

.link-url {
  font-size: 0.75rem;
  color: var(--text-muted);
  word-break: break-all;
}

/* 근무 조건 */
.condition-row { display: flex; gap: 24px; flex-wrap: wrap; }

.condition-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.condition-label { font-size: 0.8rem; color: var(--text-secondary); }
.condition-value { font-size: 1.1rem; font-weight: 700; }

/* 상태 변경 */
.status-card { border-color: var(--primary); }
.status-card h3 { font-weight: 700; margin-bottom: 12px; }

.status-actions { max-width: 200px; }

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

@media (max-width: 768px) {
  .summary-top { flex-direction: column; gap: 12px; }
  .summary-meta { flex-direction: column; gap: 8px; }
  .condition-row { flex-direction: column; }
}
</style>
