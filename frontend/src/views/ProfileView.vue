<template>
  <div class="page">
    <div class="container">
      <div class="profile-container slide-up">
        <h1 class="profile-title">내 프로필</h1>
        <p class="profile-sub">이력서 정보를 관리하세요. 기업에게 나를 어필할 수 있습니다.</p>

        <div v-if="loading" class="loading-state">불러오는 중...</div>
        <div v-else-if="error" class="error-msg">{{ error }}</div>

        <form v-else @submit.prevent="handleSave" class="card profile-card">
          <!-- 기본 정보 -->
          <div class="section">
            <h2 class="section-label">👤 기본 정보</h2>
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">이름</label>
                <input :value="profile.name" class="form-input" disabled />
              </div>
              <div class="form-group">
                <label class="form-label">이메일</label>
                <input :value="profile.email" class="form-input" disabled />
              </div>
            </div>
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">연락처</label>
                <input v-model="form.phone" type="tel" class="form-input" placeholder="010-0000-0000" />
              </div>
              <div class="form-group">
                <label class="form-label">경력 (년)</label>
                <input v-model.number="form.experienceYears" type="number" min="0" class="form-input" placeholder="0" />
              </div>
            </div>
          </div>

          <!-- 자기소개 -->
          <div class="section">
            <h2 class="section-label">📝 자기소개</h2>
            <div class="form-group">
              <textarea
                v-model="form.introduction"
                class="form-textarea"
                rows="6"
                placeholder="본인의 경력, 전문 분야, 핵심 역량 등을 자유롭게 소개해주세요..."
              ></textarea>
            </div>
          </div>

          <!-- 포트폴리오 -->
          <div class="section">
            <h2 class="section-label">🔗 포트폴리오</h2>
            <div class="form-group">
              <input v-model="form.portfolioUrl" type="url" class="form-input" placeholder="https://github.com/..." />
            </div>
          </div>

          <!-- 기술 스택 -->
          <div class="section">
            <h2 class="section-label">🛠️ 보유 기술</h2>
            <div class="skill-selector">
              <div v-for="(skills, category) in groupedSkills" :key="category" class="skill-category">
                <span class="skill-cat-label">{{ categoryLabel(category) }}</span>
                <div class="skill-chips">
                  <button
                    v-for="skill in skills" :key="skill.id"
                    type="button"
                    :class="['skill-chip', { selected: form.skillIds.has(skill.id) }]"
                    @click="toggleSkill(skill.id)"
                  >
                    {{ skill.name }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="saveSuccess" class="success-msg">✅ 프로필이 저장되었습니다!</div>
          <div v-if="saveError" class="error-msg">{{ saveError }}</div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary btn-lg" :disabled="saving">
              {{ saving ? '저장 중...' : '프로필 저장' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/api'
import { categoryLabel } from '@/composables/useSkillCategories'

const profile = ref({})
const form = reactive({
  phone: '',
  introduction: '',
  portfolioUrl: '',
  experienceYears: null,
  skillIds: new Set(),
})

const allSkills = ref([])
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const saveError = ref('')
const saveSuccess = ref(false)

const groupedSkills = computed(() => {
  const map = {}
  allSkills.value.forEach((s) => {
    if (!map[s.category]) map[s.category] = []
    map[s.category].push(s)
  })
  return map
})

onMounted(async () => {
  try {
    const [profileRes, skillsRes] = await Promise.all([
      api.get('/profile/seeker'),
      api.get('/skills'),
    ])
    profile.value = profileRes.data
    allSkills.value = skillsRes.data

    form.phone = profileRes.data.phone || ''
    form.introduction = profileRes.data.introduction || ''
    form.portfolioUrl = profileRes.data.portfolioUrl || ''
    form.experienceYears = profileRes.data.experienceYears
    if (profileRes.data.skills) {
      profileRes.data.skills.forEach((s) => form.skillIds.add(s.id))
    }
  } catch (e) {
    error.value = '프로필을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
})

function toggleSkill(id) {
  if (form.skillIds.has(id)) form.skillIds.delete(id)
  else form.skillIds.add(id)
}


async function handleSave() {
  saveError.value = ''
  saveSuccess.value = false
  saving.value = true
  try {
    const payload = { ...form, skillIds: Array.from(form.skillIds) }
    const { data } = await api.put('/profile/seeker', payload)
    profile.value = data
    saveSuccess.value = true
    setTimeout(() => (saveSuccess.value = false), 3000)
  } catch (e) {
    saveError.value = e.response?.data?.message || '저장에 실패했습니다.'
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 4px;
}

.profile-sub {
  color: var(--text-secondary);
  margin-bottom: 28px;
}

.profile-card {
  padding: 32px;
}

.section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border);
}

.section:last-of-type {
  border-bottom: none;
  margin-bottom: 16px;
}

.section-label {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--text);
}

.form-actions {
  display: flex;
  gap: 12px;
}

.success-msg {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: var(--success);
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
  margin-bottom: 16px;
}

.skill-selector {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skill-cat-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 6px;
  display: block;
}

.skill-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.skill-chip {
  padding: 6px 12px;
  border-radius: var(--radius-full);
  background: var(--bg);
  border: 1px solid var(--border);
  color: var(--text-secondary);
  font-size: 0.8rem;
  cursor: pointer;
  transition: var(--transition);
  font-family: inherit;
}

.skill-chip:hover {
  border-color: var(--primary);
  color: var(--text);
}

.skill-chip.selected {
  background: rgba(79, 70, 229, 0.2);
  border-color: var(--primary);
  color: var(--primary-light);
}

.loading-state {
  text-align: center;
  padding: 60px 0;
  color: var(--text-secondary);
}
</style>
