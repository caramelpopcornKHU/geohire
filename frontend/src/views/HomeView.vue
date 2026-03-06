<template>
  <div class="home page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="container">
        <div class="hero-content slide-up">
          <div class="hero-badge badge badge-primary">🌍 공간정보 전문 채용 플랫폼</div>
          <h1 class="hero-title">
            공간정보 분야의<br />
            <span class="gradient-text">최고의 인재</span>를 만나세요
          </h1>
          <p class="hero-desc">
            GIS, 측량, 원격탐사, 드론 등 공간정보 산업에 특화된<br />
            채용 플랫폼에서 당신의 커리어를 시작하세요.
          </p>
          <div class="hero-actions">
            <RouterLink to="/jobs" class="btn btn-primary btn-lg">채용공고 보기 →</RouterLink>
            <RouterLink to="/signup" class="btn btn-outline btn-lg">무료 가입하기</RouterLink>
          </div>
        </div>

        <!-- Stats -->
        <div class="hero-stats fade-in">
          <div class="stat-item">
            <span class="stat-value">500+</span>
            <span class="stat-label">채용공고</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">120+</span>
            <span class="stat-label">등록 기업</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">2,000+</span>
            <span class="stat-label">구직자</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">95%</span>
            <span class="stat-label">만족도</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="categories">
      <div class="container">
        <h2 class="section-title">공간정보 분야별 채용</h2>
        <p class="section-desc">전문 분야에 맞는 채용공고를 탐색하세요</p>
        <div class="grid grid-3 category-grid">
          <div v-for="cat in categories" :key="cat.name" class="card category-card" @click="goToJobs(cat.query)">
            <span class="category-icon">{{ cat.icon }}</span>
            <h3 class="category-name">{{ cat.name }}</h3>
            <p class="category-desc">{{ cat.desc }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { useRouter, RouterLink } from 'vue-router'

const router = useRouter()

const categories = [
  { icon: '🗺️', name: 'GIS 소프트웨어', desc: 'ArcGIS, QGIS, GeoServer 등 GIS SW 전문가', query: 'GIS' },
  { icon: '📡', name: '측량', desc: 'GPS/GNSS, LiDAR, 토탈스테이션 측량 전문가', query: '측량' },
  { icon: '🛰️', name: '원격탐사', desc: '위성영상, SAR, 다중분광 분석 전문가', query: '원격탐사' },
  { icon: '🚁', name: '드론', desc: '드론매핑, Pix4D, 항공촬영 전문가', query: '드론' },
  { icon: '💻', name: 'GIS 개발', desc: 'PostGIS, Leaflet, OpenLayers 개발자', query: 'PostGIS' },
  { icon: '📜', name: '자격증 보유자', desc: '측량기사, 공간정보처리기사 자격 보유', query: '측량기사' },
]

function goToJobs(keyword) {
  router.push({ name: 'jobs', query: { keyword } })
}
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 80px 0 60px;
  background: radial-gradient(ellipse at top, rgba(79, 70, 229, 0.15), transparent 60%);
}

.hero-badge {
  margin-bottom: 24px;
  font-size: 0.85rem;
}

.hero-title {
  font-size: 3.2rem;
  font-weight: 900;
  line-height: 1.2;
  letter-spacing: -0.03em;
  margin-bottom: 20px;
}

.gradient-text {
  background: linear-gradient(135deg, var(--primary-light), var(--accent), var(--success));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 1.15rem;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 36px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 64px;
  padding-top: 40px;
  border-top: 1px solid var(--border);
}

.stat-value {
  display: block;
  font-size: 2rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--primary-light), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-muted);
}

/* Categories */
.categories {
  padding: 80px 0;
}

.section-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 8px;
}

.section-desc {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 40px;
}

.category-card {
  cursor: pointer;
  text-align: center;
  padding: 32px 24px;
}

.category-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.category-icon {
  font-size: 2.5rem;
  display: block;
  margin-bottom: 16px;
}

.category-name {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.category-desc {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .hero-title { font-size: 2rem; }
  .hero-stats { flex-wrap: wrap; gap: 24px; }
  .hero-actions { flex-direction: column; align-items: center; }
}
</style>
