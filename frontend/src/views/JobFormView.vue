<template>
  <div class="page">
    <div class="container">
      <div class="form-container slide-up">
        <h1 class="form-title">{{ isEdit ? '채용공고 수정' : '새 채용공고 등록' }}</h1>

        <div v-if="error" class="error-msg">{{ error }}</div>

        <form @submit.prevent="handleSubmit" class="card" style="padding: 32px;">
          <div class="form-group">
            <label class="form-label">제목 *</label>
            <input id="job-title" v-model="form.title" type="text" class="form-input" placeholder="예: GIS 분석 엔지니어 채용" required />
          </div>

          <div class="grid grid-2">
            <div class="form-group">
              <label class="form-label">고용 형태 *</label>
              <select id="job-type" v-model="form.employmentType" class="form-select" required>
                <option value="">선택</option>
                <option value="FULL_TIME">정규직</option>
                <option value="CONTRACT">계약직</option>
                <option value="INTERN">인턴</option>
                <option value="FREELANCE">프리랜서</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">근무 지역</label>
              <input v-model="form.location" type="text" class="form-input" placeholder="예: 서울 강남구" />
            </div>
          </div>

          <div class="grid grid-2">
            <div class="form-group">
              <label class="form-label">최소 연봉 (원)</label>
              <input v-model.number="form.salaryMin" type="number" class="form-input" placeholder="30000000" />
            </div>
            <div class="form-group">
              <label class="form-label">최대 연봉 (원)</label>
              <input v-model.number="form.salaryMax" type="number" class="form-input" placeholder="50000000" />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">마감일</label>
            <input v-model="form.deadline" type="date" class="form-input" />
          </div>

          <div class="form-group">
            <label class="form-label">요구 기술</label>
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

          <div class="form-group">
            <label class="form-label">상세 내용 *</label>
            <textarea id="job-desc" v-model="form.description" class="form-textarea" rows="10"
              placeholder="담당 업무, 자격 요건, 우대 사항 등을 자세히 작성해주세요..." required></textarea>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary btn-lg" :disabled="saving">
              {{ saving ? '저장 중...' : (isEdit ? '수정하기' : '등록하기') }}
            </button>
            <RouterLink to="/dashboard" class="btn btn-secondary btn-lg">취소</RouterLink>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import api from '@/api'
import { categoryLabel } from '@/composables/useSkillCategories'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  description: '',
  location: '',
  employmentType: '',
  salaryMin: null,
  salaryMax: null,
  deadline: '',
  skillIds: new Set(),
  status: 'OPEN',
})

const allSkills = ref([])
const error = ref('')
const saving = ref(false)

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
    const { data } = await api.get('/skills')
    allSkills.value = data
  } catch (e) {
    console.error(e)
  }

  if (isEdit.value) {
    try {
      const { data } = await api.get(`/jobs/${route.params.id}`)
      form.title = data.title
      form.description = data.description
      form.location = data.location || ''
      form.employmentType = data.employmentType
      form.salaryMin = data.salaryMin
      form.salaryMax = data.salaryMax
      form.deadline = data.deadline || ''
      form.status = data.status
      if (data.skills) data.skills.forEach((s) => form.skillIds.add(s.id))
    } catch (e) {
      error.value = '공고 정보를 불러오지 못했습니다.'
    }
  }
})

function toggleSkill(id) {
  if (form.skillIds.has(id)) form.skillIds.delete(id)
  else form.skillIds.add(id)
}


async function handleSubmit() {
  error.value = ''
  saving.value = true
  try {
    const payload = {
      ...form,
      skillIds: Array.from(form.skillIds),
      salaryMin: form.salaryMin || null,
      salaryMax: form.salaryMax || null,
      deadline: form.deadline || null,
    }

    if (isEdit.value) {
      await api.put(`/jobs/${route.params.id}`, payload)
    } else {
      await api.post('/jobs', payload)
    }
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.message || '저장에 실패했습니다.'
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.form-container {
  max-width: 800px;
  margin: 0 auto;
}

.form-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 24px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

/* Skill Selector */
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
</style>
