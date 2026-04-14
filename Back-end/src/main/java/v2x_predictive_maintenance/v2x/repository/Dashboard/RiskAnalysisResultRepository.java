package v2x_predictive_maintenance.v2x.repository.Dashboard;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import v2x_predictive_maintenance.v2x.entity.Dashboard.RiskAnalysisResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RiskAnalysisResultRepository extends JpaRepository<RiskAnalysisResult, Long> {

    long countByRiskLevel(String riskLevel); // 특정 상태 개수

    long countByRiskLevelNot(String riskLevel); // 특정 상태 제외 개수

    @Query("SELECT AVG(r.totalRiskScore) FROM RiskAnalysisResult r")
    BigDecimal findAverageRiskScore(); // 평균 위험 점수

    List<RiskAnalysisResult> findTop5ByRiskLevelNotOrderByAnalysisTimeDesc(String riskLevel); // 최신 이상 목록 5개

    Optional<RiskAnalysisResult> findTopByIntersectionIdOrderByAnalysisTimeDesc(String intersectionId); // 교차로별 최신 위험도 조회

    // 최근 24시간 위험 점수 조회 (시간순)
    List<RiskAnalysisResult> findTop7ByIntersectionIdOrderByAnalysisTimeAsc(String intersectionId);

    // 전체 결과 최신순 조회 (테이블용)
    List<RiskAnalysisResult> findAllByOrderByAnalysisTimeDesc();

    /**
     * JPQL DISTINCT 일부 환경에서 빈 결과가 나오는 경우가 있어 public 스키마에 대해 네이티브로 조회.
     */
    @Query(
            value = "SELECT DISTINCT TRIM(intersection_id) FROM risk_analysis_result "
                    + "WHERE intersection_id IS NOT NULL AND LENGTH(TRIM(intersection_id)) > 0 "
                    + "ORDER BY 1",
            nativeQuery = true)
    List<String> findDistinctIntersectionIds();

    /**
     * PostgreSQL: 교차로마다 analysis_time 최신 1건(동시각이면 risk_result_id 큰 쪽).
     * 통합 모니터링 KPI·지도와 동일한 스냅샷.
     */
    @Query(
            value = "SELECT * FROM ( "
                    + "SELECT DISTINCT ON (intersection_id) * FROM risk_analysis_result "
                    + "ORDER BY intersection_id, analysis_time DESC, risk_result_id DESC "
                    + ") AS latest_per_intersection",
            nativeQuery = true)
    List<RiskAnalysisResult> findLatestRowPerIntersection();

    /** TRIM 후 등급이 정상이 아닌 최신 분석들 — Pageable 로 상위 N건 */
    @Query(
            "SELECT r FROM RiskAnalysisResult r WHERE TRIM(r.riskLevel) <> :normalLabel "
                    + "ORDER BY r.analysisTime DESC, r.riskResultId DESC")
    List<RiskAnalysisResult> findAbnormalByNewestFirst(
            @Param("normalLabel") String normalLabel, Pageable pageable);

}