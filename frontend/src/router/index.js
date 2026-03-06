import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/SignupView.vue'),
    },
    {
      path: '/jobs',
      name: 'jobs',
      component: () => import('@/views/JobListView.vue'),
    },
    {
      path: '/jobs/new',
      name: 'job-create',
      component: () => import('@/views/JobFormView.vue'),
      meta: { requiresAuth: true, role: 'COMPANY' },
    },
    {
      path: '/jobs/:id',
      name: 'job-detail',
      component: () => import('@/views/JobDetailView.vue'),
    },
    {
      path: '/jobs/:id/apply',
      name: 'apply',
      component: () => import('@/views/ApplyView.vue'),
      meta: { requiresAuth: true, role: 'SEEKER' },
    },
    {
      path: '/jobs/:id/edit',
      name: 'job-edit',
      component: () => import('@/views/JobFormView.vue'),
      meta: { requiresAuth: true, role: 'COMPANY' },
    },
    {
      path: '/jobs/:id/applicants',
      name: 'job-applicants',
      component: () => import('@/views/ApplicantsView.vue'),
      meta: { requiresAuth: true, role: 'COMPANY' },
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true, role: 'COMPANY' },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true, role: 'SEEKER' },
    },
    {
      path: '/applications',
      name: 'applications',
      component: () => import('@/views/ApplicationsView.vue'),
      meta: { requiresAuth: true, role: 'SEEKER' },
    },
    {
      path: '/applications/:id',
      name: 'application-detail',
      component: () => import('@/views/ApplicationDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: () => import('@/views/NotificationsView.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const token = localStorage.getItem('accessToken')

  if (to.meta.requiresAuth && !token) {
    return next({ name: 'login', query: { redirect: to.fullPath } })
  }

  if (to.meta.role && user?.role !== to.meta.role) {
    return next({ name: 'home' })
  }

  next()
})

export default router
