package v2x_predictive_maintenance.v2x.service.Predictive;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v2x_predictive_maintenance.v2x.dto.Predictive.PredictiveAnalysisDTO;
import v2x_predictive_maintenance.v2x.dto.Predictive.RiskHistoryDTO;
import v2x_predictive_maintenance.v2x.entity.Dashboard.RiskAnalysisResult;
import v2x_predictive_maintenance.v2x.repository.Dashboard.RiskAnalysisResultRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictiveAnalysisService {

    private final RiskAnalysisResultRepository riskAnalysisResultRepository;

    // 예지보전 분석 결과 조회
    public PredictiveAnalysisDTO getPredictiveAnalysis(String intersectionId) {

        // 1️⃣ 최신 데이터 조회
        RiskAnalysisResult result = riskAnalysisResultRepository
                .findTopByIntersectionIdOrderByAnalysisTimeDesc(intersectionId)
                .orElseThrow(() -> new RuntimeException("분석 결과 없음"));

        // 2️⃣ 값 변환
        double totalRisk = result.getTotalRiskScore().doubleValue(); // 종합 위험 점수

        // 🔥 프론트 기준으로 계산
        double remainDays = 100 - totalRisk; // 잔여 일수
        double failProb = totalRisk;         // 고장 확률

        // 3️⃣ DTO 변환
        return new PredictiveAnalysisDTO(
                result.getIntersectionId(),                     // 교차로 ID
                totalRisk,                                     // 종합 위험 점수
                result.getRiskLevel(),                         // 위험 단계
                result.getAnalysisComment(),                   // 분석 코멘트
                result.getControllerRiskScore().doubleValue(), // 제어기 점수
                result.getV2xRiskScore().doubleValue(),        // V2X 점수
                result.getAnalysisTime().toString(),           // 분석 시간
                remainDays,                                   // 잔여 일수
                failProb                                      // 고장 확률
        );
    }

    // 최근 위험 점수 히스토리 조회 (그래프용)
    public List<RiskHistoryDTO> getRiskHistory(String intersectionId) {

        // 1️⃣ 최근 데이터 조회
        List<RiskAnalysisResult> results =
                riskAnalysisResultRepository.findTop7ByIntersectionIdOrderByAnalysisTimeAsc(intersectionId);

        // 2️⃣ DTO 변환
        return results.stream()
                .map(r -> new RiskHistoryDTO(
                        r.getAnalysisTime().toLocalTime().toString().substring(0, 5), // 시간 (HH:mm)
                        r.getTotalRiskScore().doubleValue() // 점수
                ))
                .toList();
    }

}