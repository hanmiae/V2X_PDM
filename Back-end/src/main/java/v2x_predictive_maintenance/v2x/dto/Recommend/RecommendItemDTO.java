package v2x_predictive_maintenance.v2x.dto.Recommend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendItemDTO {

    private Long id; // 결과 ID

    private String intersectionId; // 교차로 ID

    private String intersectionName; // 교차로명

    private String deviceId; // 장비 ID

    private String deviceType; // 장비 종류

    private String riskLevel; // 위험 등급

    private String riskLevelClass; // danger / warning / normal

    private String analysisComment; // AI 분석 메시지

    private double remainDays; // 예상 잔여 수명(일)

    private String analysisTime; // 분석 시간

    private String actionMessage; // 조치 권고 문구

}