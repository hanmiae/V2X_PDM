package v2x_predictive_maintenance.v2x.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {

    /** intersection_info 기준 관측 교차로 수(없으면 risk 보조) */
    private Long totalDevices;

    /** 최신 분석이 없거나 등급이 정상인 교차로 수 */
    private Long normalDevices;

    /** 최신 등급이 정상이 아닌 교차로 수 */
    private Long abnormalDevices;

    /** abnormalDevices / totalDevices × 100 */
    private Double abnormalRate;

    /** 교차로별 최신 total_risk_score 평균(미분석은 0으로 포함) */
    private BigDecimal averageRiskScore;
}