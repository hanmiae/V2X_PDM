package v2x_predictive_maintenance.v2x.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbnormalDeviceDTO {

    private String intersectionId; // 교차로 ID

    private String deviceId; // 기기 ID

    private BigDecimal riskScore; // 위험 점수

    private String riskLevel; // 상태

    private LocalDateTime analysisTime; // 탐지 시간

}