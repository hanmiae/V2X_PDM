package v2x_predictive_maintenance.v2x.dto.V2X;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2XHistoryDTO {

    private String time; // 시간 (예: 04:00)

    private double latency; // 지연 시간(ms)

}