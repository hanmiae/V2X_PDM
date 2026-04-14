package v2x_predictive_maintenance.v2x.dto.Predictive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskHistoryDTO {

    private String time; // 시간 (예: 04시, 08시)

    private double score; // 해당 시간의 위험 점수
}