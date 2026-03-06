<template>
  <nav class="navbar">
    <div class="container navbar-inner">
      <RouterLink to="/" class="navbar-brand">
        <span class="brand-icon">🌐</span>
        <span class="brand-text">Geo<span class="brand-accent">Hire</span></span>
      </RouterLink>

      <div class="navbar-links">
        <RouterLink to="/jobs" class="nav-link">채용공고</RouterLink>

        <template v-if="auth.isLoggedIn">
          <!-- 구직자 메뉴 -->
          <template v-if="auth.isSeeker">
            <RouterLink to="/profile" class="nav-link">내 프로필</RouterLink>
            <RouterLink to="/applications" class="nav-link">지원내역</RouterLink>
          </template>

          <!-- 기업 메뉴 -->
          <template v-if="auth.isCompany">
            <RouterLink to="/jobs/new" class="nav-link">공고 등록</RouterLink>
            <RouterLink to="/dashboard" class="nav-link">대시보드</RouterLink>
          </template>

          <!-- 알림 -->
          <RouterLink to="/notifications" class="nav-link notif-link">
            🔔
            <span v-if="unreadCount > 0" class="notif-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
          </RouterLink>

          <div class="nav-user">
            <span class="user-name">{{ auth.user?.name }}</span>
            <span class="user-role badge" :class="auth.isCompany ? 'badge-accent' : 'badge-primary'">
              {{ auth.isCompany ? '기업' : '구직자' }}
            </span>
            <button class="btn btn-sm btn-secondary" @click="handleLogout">로그아웃</button>
          </div>
        </template>

        <template v-else>
          <RouterLink to="/login" class="btn btn-sm btn-outline">로그인</RouterLink>
          <RouterLink to="/signup" class="btn btn-sm btn-primary">회원가입</RouterLink>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import api from '@/api'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const unreadCount = ref(0)

async function fetchUnread() {
  if (!auth.isLoggedIn) return
  try {
    const { data } = await api.get('/notifications/unread-count')
    unreadCount.value = data.count
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  fetchUnread()
  // 30초마다 폴링
  setInterval(fetchUnread, 30000)
})

// 페이지 전환 시 갱신
watch(() => route.path, fetchUnread)

function handleLogout() {
  auth.logout()
  unreadCount.value = 0
  router.push('/')
}
</script>

<style scoped>
.navbar {
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--text);
}

.brand-icon {
  font-size: 1.5rem;
}

.brand-accent {
  background: linear-gradient(135deg, var(--primary-light), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9rem;
  transition: var(--transition);
  padding: 6px 12px;
  border-radius: var(--radius-sm);
}

.nav-link:hover,
.nav-link.router-link-active {
  color: var(--text);
  background: var(--bg-elevated);
}

.notif-link {
  position: relative;
  font-size: 1.1rem;
  padding: 6px 10px;
}

.notif-badge {
  position: absolute;
  top: 0;
  right: 2px;
  background: var(--danger);
  color: white;
  font-size: 0.6rem;
  font-weight: 700;
  min-width: 16px;
  height: 16px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  line-height: 1;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: 12px;
  border-left: 1px solid var(--border);
}

.user-name {
  font-weight: 600;
  font-size: 0.875rem;
}

.user-role {
  font-size: 0.7rem;
}
</style>
