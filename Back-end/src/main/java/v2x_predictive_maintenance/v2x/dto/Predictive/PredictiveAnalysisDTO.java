package v2x_predictive_maintenance.v2x.dto.Predictive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveAnalysisDTO {

    private String intersectionId; // 교차로 ID

    private double totalRiskScore; // 종합 위험 점수 (0~100)

    private String riskLevel; // 위험 단계 (정상 / 주의 / 경고 / 위험)

    private String analysisComment; // AI 분석 결과 메시지

    private double controllerRiskScore; // 제어기 위험 점수

    private double v2xRiskScore; // V2X 위험 점수

    private String analysisTime; // 분석 시간

    @JsonProperty("remain_days")
    private double remainDays; // 잔여 일수

    @JsonProperty("fail_prob")
    private double failProb; // 고장 확률 (%)
}