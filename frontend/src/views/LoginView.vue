<template>
  <div class="page">
    <div class="container auth-container">
      <div class="auth-card card slide-up">
        <div class="auth-header">
          <h1>로그인</h1>
          <p>GeoHire에 오신 것을 환영합니다</p>
        </div>

        <div v-if="error" class="error-msg">{{ error }}</div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="form-label">이메일</label>
            <input id="login-email" v-model="form.email" type="email" class="form-input" placeholder="you@example.com" required />
          </div>
          <div class="form-group">
            <label class="form-label">비밀번호</label>
            <input id="login-password" v-model="form.password" type="password" class="form-input" placeholder="비밀번호 입력" required />
          </div>
          <button id="login-submit" type="submit" class="btn btn-primary btn-lg" style="width: 100%" :disabled="loading">
            {{ loading ? '로그인 중...' : '로그인' }}
          </button>
        </form>

        <p class="auth-footer">
          계정이 없으신가요? <RouterLink to="/signup">회원가입</RouterLink>
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

const form = reactive({ email: '', password: '' })
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(form)
    const redirect = router.currentRoute.value.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    error.value = e.response?.data?.message || '로그인에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  padding-top: 60px;
}

.auth-card {
  width: 100%;
  max-width: 440px;
  padding: 40px;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
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

.auth-footer a:hover {
  text-decoration: underline;
}
</style>
