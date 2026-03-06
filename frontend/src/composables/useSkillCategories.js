export const CATEGORY_LABELS = {
  GIS_DESKTOP: '🖥️ 데스크탑 GIS',
  GIS_SERVER: '🌐 GIS 서버 / SDI',
  SPATIAL_DB: '🗄️ 공간 데이터베이스',
  WEB_GIS: '🌍 Web GIS',
  REMOTE_SENSING: '🛰️ 원격탐사',
  SURVEYING: '📡 측량',
  DRONE: '🚁 드론 / UAV',
  PHOTOGRAMMETRY: '📸 사진측량',
  LIDAR: '📐 LiDAR',
  SPATIAL_ANALYSIS: '📊 공간분석',
  PROGRAMMING: '💻 프로그래밍',
  GEO_AI: '🤖 GeoAI / 딥러닝',
  THREE_D: '🏗️ 3D / BIM / 디지털트윈',
  CAD: '📏 CAD / 도면',
  CLOUD_INFRA: '☁️ 클라우드 / 인프라',
  DATA_FORMAT: '📦 데이터 포맷 / 표준',
  NAVIGATION: '📍 측위 / 내비게이션',
  CERTIFICATION: '📜 자격증',
  OTHER: '🔧 기타',
}

export function categoryLabel(cat) {
  return CATEGORY_LABELS[cat] || cat
}
