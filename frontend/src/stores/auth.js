import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const accessToken = ref(localStorage.getItem('accessToken') || null)

  const isLoggedIn = computed(() => !!accessToken.value)
  const isCompany = computed(() => user.value?.role === 'COMPANY')
  const isSeeker = computed(() => user.value?.role === 'SEEKER')

  async function signup(data) {
    const res = await api.post('/auth/signup', data)
    setAuth(res.data)
    return res.data
  }

  async function login(data) {
    const res = await api.post('/auth/login', data)
    setAuth(res.data)
    return res.data
  }

  function setAuth(authData) {
    accessToken.value = authData.accessToken
    user.value = {
      id: authData.userId,
      name: authData.name,
      email: authData.email,
      role: authData.role,
    }
    localStorage.setItem('accessToken', authData.accessToken)
    localStorage.setItem('refreshToken', authData.refreshToken)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function logout() {
    user.value = null
    accessToken.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
  }

  return { user, accessToken, isLoggedIn, isCompany, isSeeker, signup, login, logout }
})
