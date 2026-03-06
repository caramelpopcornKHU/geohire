<template>
  <div class="page">
    <div class="container auth-container">
      <div class="auth-card card slide-up">
        <div class="auth-header">
          <h1>회원가입</h1>
          <p>GeoHire에서 새로운 기회를 찾아보세요</p>
        </div>

        <div v-if="error" class="error-msg">{{ error }}</div>

        <!-- 역할 선택 -->
        <div class="role-selector">
          <button
            v-for="r in roles" :key="r.value"
            :class="['role-btn', { active: form.role === r.value }]"
            @click="form.role = r.value"
            type="button"
          >
            <span class="role-icon">{{ r.icon }}</span>
            <span class="role-label">{{ r.label }}</span>
          </button>
        </div>

        <form @submit.prevent="handleSignup">
          <div class="form-group">
            <label class="form-label">이름</label>
            <input id="signup-name" v-model="form.name" type="text" class="form-input" placeholder="이름 입력" required />
          </div>
          <div class="form-group">
            <label class="form-label">이메일</label>
            <input id="signup-email" v-model="form.email" type="email" class="form-input" placeholder="you@example.com" required />
          </div>
          <div class="form-group">
            <label class="form-label">비밀번호</label>
            <input id="signup-password" v-model="form.password" type="password" class="form-input" placeholder="비밀번호 입력" required />
          </div>
          <div v-if="form.role === 'COMPANY'" class="form-group">
            <label class="form-label">기업명</label>
            <input id="signup-company" v-model="form.companyName" type="text" class="form-input" placeholder="기업명 입력" required />
          </div>
          <button id="signup-submit" type="submit" class="btn btn-primary btn-lg" style="width: 100%" :disabled="loading">
            {{ loading ? '가입 중...' : '가입하기' }}
          </button>
        </form>

        <p class="auth-footer">
          이미 계정이 있으신가요? <RouterLink to="/login">로그인</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const roles = [
  { value: 'SEEKER', label: '구직자', icon: '👤' },
  { value: 'COMPANY', label: '기업', icon: '🏢' },
]

const form = reactive({ name: '', email: '', password: '', role: 'SEEKER', companyName: '' })
const error = ref('')
const loading = ref(false)

async function handleSignup() {
  error.value = ''
  loading.value = true
  try {
    await auth.signup(form)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || '회원가입에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  padding: 40px;
}

.auth-header {
  text-align: center;
  margin-bottom: 24px;
}

.auth-header h1 {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 8px;
}

.auth-header p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 28px;
}

.role-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px;
  background: var(--bg);
  border: 2px solid var(--border);
  border-radius: var(--radius);
  cursor: pointer;
  transition: var(--transition);
  color: var(--text-secondary);
  font-family: inherit;
}

.role-btn:hover {
  border-color: var(--border-light);
}

.role-btn.active {
  border-color: var(--primary);
  background: rgba(79, 70, 229, 0.1);
  color: var(--text);
}

.role-icon {
  font-size: 1.5rem;
}

.role-label {
  font-weight: 600;
  font-size: 0.9rem;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.auth-footer a {
  color: var(--primary-light);
  text-decoration: none;
  font-weight: 600;
}
</style>
