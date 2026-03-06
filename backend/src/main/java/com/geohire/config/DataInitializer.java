package com.geohire.config;

import com.geohire.entity.Skill;
import com.geohire.entity.SkillCategory;
import com.geohire.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SkillRepository skillRepository;

    @Override
    public void run(String... args) {
        if (skillRepository.count() > 0) return;

        List<Skill> skills = List.of(
            // ===== 데스크탑 GIS =====
            s("ArcGIS Pro", SkillCategory.GIS_DESKTOP),
            s("ArcGIS Desktop (ArcMap)", SkillCategory.GIS_DESKTOP),
            s("QGIS", SkillCategory.GIS_DESKTOP),
            s("MapInfo Pro", SkillCategory.GIS_DESKTOP),
            s("GRASS GIS", SkillCategory.GIS_DESKTOP),
            s("Global Mapper", SkillCategory.GIS_DESKTOP),
            s("ERDAS IMAGINE", SkillCategory.GIS_DESKTOP),
            s("ENVI", SkillCategory.GIS_DESKTOP),
            s("SuperMap", SkillCategory.GIS_DESKTOP),
            s("uDig", SkillCategory.GIS_DESKTOP),

            // ===== GIS 서버 / SDI =====
            s("ArcGIS Enterprise", SkillCategory.GIS_SERVER),
            s("ArcGIS Online (AGOL)", SkillCategory.GIS_SERVER),
            s("GeoServer", SkillCategory.GIS_SERVER),
            s("MapServer", SkillCategory.GIS_SERVER),
            s("QGIS Server", SkillCategory.GIS_SERVER),
            s("GeoNode", SkillCategory.GIS_SERVER),
            s("Mapbox Studio", SkillCategory.GIS_SERVER),
            s("OpenGeo Suite", SkillCategory.GIS_SERVER),

            // ===== 공간 데이터베이스 =====
            s("PostGIS", SkillCategory.SPATIAL_DB),
            s("Oracle Spatial", SkillCategory.SPATIAL_DB),
            s("SQL Server Spatial", SkillCategory.SPATIAL_DB),
            s("SpatiaLite", SkillCategory.SPATIAL_DB),
            s("MongoDB (GeoJSON)", SkillCategory.SPATIAL_DB),
            s("Elasticsearch (Geo)", SkillCategory.SPATIAL_DB),
            s("H2GIS", SkillCategory.SPATIAL_DB),
            s("Google BigQuery GIS", SkillCategory.SPATIAL_DB),

            // ===== Web GIS / 프론트엔드 =====
            s("OpenLayers", SkillCategory.WEB_GIS),
            s("Leaflet", SkillCategory.WEB_GIS),
            s("Mapbox GL JS", SkillCategory.WEB_GIS),
            s("Cesium.js", SkillCategory.WEB_GIS),
            s("Deck.gl", SkillCategory.WEB_GIS),
            s("Turf.js", SkillCategory.WEB_GIS),
            s("ArcGIS Maps SDK for JS", SkillCategory.WEB_GIS),
            s("Google Maps API", SkillCategory.WEB_GIS),
            s("Naver Maps API", SkillCategory.WEB_GIS),
            s("Kakao Maps API", SkillCategory.WEB_GIS),
            s("MapLibre GL", SkillCategory.WEB_GIS),
            s("Kepler.gl", SkillCategory.WEB_GIS),
            s("three.js (GIS)", SkillCategory.WEB_GIS),

            // ===== 원격탐사 =====
            s("위성영상 분석", SkillCategory.REMOTE_SENSING),
            s("SAR 영상처리", SkillCategory.REMOTE_SENSING),
            s("다중분광 분석", SkillCategory.REMOTE_SENSING),
            s("초분광 분석", SkillCategory.REMOTE_SENSING),
            s("Google Earth Engine", SkillCategory.REMOTE_SENSING),
            s("SNAP (ESA)", SkillCategory.REMOTE_SENSING),
            s("eCognition", SkillCategory.REMOTE_SENSING),
            s("NDVI / 식생지수 분석", SkillCategory.REMOTE_SENSING),
            s("변화탐지 (Change Detection)", SkillCategory.REMOTE_SENSING),
            s("토지피복 분류", SkillCategory.REMOTE_SENSING),
            s("Sentinel Hub", SkillCategory.REMOTE_SENSING),

            // ===== 측량 =====
            s("GPS/GNSS 측량", SkillCategory.SURVEYING),
            s("RTK 측량", SkillCategory.SURVEYING),
            s("VRS/네트워크 RTK", SkillCategory.SURVEYING),
            s("토탈스테이션", SkillCategory.SURVEYING),
            s("수준측량", SkillCategory.SURVEYING),
            s("지적측량", SkillCategory.SURVEYING),
            s("지형측량", SkillCategory.SURVEYING),
            s("노선측량", SkillCategory.SURVEYING),
            s("해양측량 / 수심측량", SkillCategory.SURVEYING),
            s("Trimble 장비", SkillCategory.SURVEYING),
            s("Leica 장비", SkillCategory.SURVEYING),

            // ===== 드론 / UAV =====
            s("드론 매핑", SkillCategory.DRONE),
            s("드론 촬영 / 운용", SkillCategory.DRONE),
            s("DJI Terra", SkillCategory.DRONE),
            s("Pix4D", SkillCategory.DRONE),
            s("DroneDeploy", SkillCategory.DRONE),
            s("Agisoft Metashape", SkillCategory.DRONE),
            s("드론 정사영상 제작", SkillCategory.DRONE),
            s("드론 3D 모델링", SkillCategory.DRONE),

            // ===== 사진측량 =====
            s("항공사진측량", SkillCategory.PHOTOGRAMMETRY),
            s("SfM (Structure from Motion)", SkillCategory.PHOTOGRAMMETRY),
            s("정사영상 (Orthophoto) 제작", SkillCategory.PHOTOGRAMMETRY),
            s("DEM/DSM 생성", SkillCategory.PHOTOGRAMMETRY),
            s("스테레오 매칭", SkillCategory.PHOTOGRAMMETRY),
            s("번들 조정 (Bundle Adjustment)", SkillCategory.PHOTOGRAMMETRY),

            // ===== LiDAR =====
            s("LiDAR 데이터 처리", SkillCategory.LIDAR),
            s("포인트클라우드 분류", SkillCategory.LIDAR),
            s("LAStools", SkillCategory.LIDAR),
            s("CloudCompare", SkillCategory.LIDAR),
            s("TerraSolid / TerraScan", SkillCategory.LIDAR),
            s("PDAL", SkillCategory.LIDAR),
            s("모바일 LiDAR (MMS)", SkillCategory.LIDAR),
            s("지상 LiDAR (TLS)", SkillCategory.LIDAR),

            // ===== 공간분석 / 모델링 =====
            s("공간통계 분석", SkillCategory.SPATIAL_ANALYSIS),
            s("네트워크 분석", SkillCategory.SPATIAL_ANALYSIS),
            s("수치지형 분석", SkillCategory.SPATIAL_ANALYSIS),
            s("유역 분석 (Watershed)", SkillCategory.SPATIAL_ANALYSIS),
            s("적지 분석 (Suitability)", SkillCategory.SPATIAL_ANALYSIS),
            s("지오코딩 / 역지오코딩", SkillCategory.SPATIAL_ANALYSIS),
            s("지형 시뮬레이션", SkillCategory.SPATIAL_ANALYSIS),
            s("동선 분석 / 경로 최적화", SkillCategory.SPATIAL_ANALYSIS),
            s("열섬 분석 (UHI)", SkillCategory.SPATIAL_ANALYSIS),
            s("침수 분석 / 재해 모델링", SkillCategory.SPATIAL_ANALYSIS),

            // ===== 프로그래밍 =====
            s("Python", SkillCategory.PROGRAMMING),
            s("Python (GeoPandas)", SkillCategory.PROGRAMMING),
            s("Python (Rasterio)", SkillCategory.PROGRAMMING),
            s("Python (Shapely/Fiona)", SkillCategory.PROGRAMMING),
            s("Python (ArcPy)", SkillCategory.PROGRAMMING),
            s("Python (PyQGIS)", SkillCategory.PROGRAMMING),
            s("R (Spatial)", SkillCategory.PROGRAMMING),
            s("Java", SkillCategory.PROGRAMMING),
            s("JavaScript / TypeScript", SkillCategory.PROGRAMMING),
            s("C++ (GIS)", SkillCategory.PROGRAMMING),
            s("C# (.NET GIS)", SkillCategory.PROGRAMMING),
            s("GDAL/OGR", SkillCategory.PROGRAMMING),
            s("GeoTools (Java)", SkillCategory.PROGRAMMING),
            s("JTS Topology Suite", SkillCategory.PROGRAMMING),
            s("Spring Boot", SkillCategory.PROGRAMMING),
            s("Node.js", SkillCategory.PROGRAMMING),
            s("Django / Flask", SkillCategory.PROGRAMMING),

            // ===== 지리 AI / 딥러닝 =====
            s("딥러닝 영상분류", SkillCategory.GEO_AI),
            s("객체 탐지 (Object Detection)", SkillCategory.GEO_AI),
            s("시맨틱 세그멘테이션", SkillCategory.GEO_AI),
            s("PyTorch (GeoAI)", SkillCategory.GEO_AI),
            s("TensorFlow (GeoAI)", SkillCategory.GEO_AI),
            s("건물 자동 추출", SkillCategory.GEO_AI),
            s("도로 자동 추출", SkillCategory.GEO_AI),
            s("점군 딥러닝 (PointNet 등)", SkillCategory.GEO_AI),

            // ===== 3D / BIM / 디지털 트윈 =====
            s("3D 도시 모델링", SkillCategory.THREE_D),
            s("CityGML", SkillCategory.THREE_D),
            s("3D Tiles", SkillCategory.THREE_D),
            s("BIM (Revit / IFC)", SkillCategory.THREE_D),
            s("디지털 트윈", SkillCategory.THREE_D),
            s("Unreal Engine (GIS)", SkillCategory.THREE_D),
            s("Unity (GIS)", SkillCategory.THREE_D),
            s("SketchUp (지형)", SkillCategory.THREE_D),
            s("Blender (GIS)", SkillCategory.THREE_D),

            // ===== CAD / 도면 =====
            s("AutoCAD", SkillCategory.CAD),
            s("AutoCAD Civil 3D", SkillCategory.CAD),
            s("MicroStation", SkillCategory.CAD),
            s("CAD-GIS 데이터 변환", SkillCategory.CAD),

            // ===== 클라우드 / 인프라 =====
            s("AWS (Location Service)", SkillCategory.CLOUD_INFRA),
            s("Google Cloud (BigQuery GIS)", SkillCategory.CLOUD_INFRA),
            s("Azure Maps", SkillCategory.CLOUD_INFRA),
            s("Docker / Kubernetes", SkillCategory.CLOUD_INFRA),
            s("GeoTiff Cloud Optimized (COG)", SkillCategory.CLOUD_INFRA),
            s("STAC (SpatioTemporal Asset Catalog)", SkillCategory.CLOUD_INFRA),

            // ===== 공간 데이터 포맷 / 표준 =====
            s("OGC 표준 (WMS/WFS/WCS)", SkillCategory.DATA_FORMAT),
            s("GeoJSON", SkillCategory.DATA_FORMAT),
            s("Shapefile", SkillCategory.DATA_FORMAT),
            s("GeoPackage", SkillCategory.DATA_FORMAT),
            s("GeoTIFF", SkillCategory.DATA_FORMAT),
            s("KML / KMZ", SkillCategory.DATA_FORMAT),
            s("LAS / LAZ", SkillCategory.DATA_FORMAT),
            s("FlatGeobuf", SkillCategory.DATA_FORMAT),
            s("MVT (Vector Tiles)", SkillCategory.DATA_FORMAT),
            s("INSPIRE / NSDI 표준", SkillCategory.DATA_FORMAT),

            // ===== 측위 / 내비게이션 / LBS =====
            s("실내 측위 (IPS)", SkillCategory.NAVIGATION),
            s("UWB 측위", SkillCategory.NAVIGATION),
            s("BLE 비콘", SkillCategory.NAVIGATION),
            s("SLAM", SkillCategory.NAVIGATION),
            s("자율주행 지도 (HD Map)", SkillCategory.NAVIGATION),
            s("경로 탐색 알고리즘", SkillCategory.NAVIGATION),
            s("위치기반서비스 (LBS)", SkillCategory.NAVIGATION),

            // ===== 자격증 =====
            s("측량기사", SkillCategory.CERTIFICATION),
            s("측량기능사", SkillCategory.CERTIFICATION),
            s("측량및지형공간정보기사", SkillCategory.CERTIFICATION),
            s("공간정보처리기사", SkillCategory.CERTIFICATION),
            s("지적기사", SkillCategory.CERTIFICATION),
            s("지적기능사", SkillCategory.CERTIFICATION),
            s("GIS분석사 1급", SkillCategory.CERTIFICATION),
            s("GIS분석사 2급", SkillCategory.CERTIFICATION),
            s("드론(무인멀티콥터)조종자", SkillCategory.CERTIFICATION),
            s("초경량비행장치 조종자", SkillCategory.CERTIFICATION),
            s("정보처리기사", SkillCategory.CERTIFICATION),
            s("토목기사", SkillCategory.CERTIFICATION)
        );

        skillRepository.saveAll(skills);
    }

    private Skill s(String name, SkillCategory category) {
        return Skill.builder().name(name).category(category).build();
    }
}
