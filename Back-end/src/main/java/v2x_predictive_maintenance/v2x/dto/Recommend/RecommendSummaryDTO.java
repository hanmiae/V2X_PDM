package v2x_predictive_maintenance.v2x.dto.Recommend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendSummaryDTO {

    private long criticalCount; // 긴급 교체 권고 건수

    private long warningCount; // 정밀 점검 대상 건수

    private long normalCount; // 정상 가동 건수
}