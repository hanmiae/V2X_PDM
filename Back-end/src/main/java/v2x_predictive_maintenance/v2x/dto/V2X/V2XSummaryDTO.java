package v2x_predictive_maintenance.v2x.dto.V2X;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2XSummaryDTO {

    private String deviceId; // 장비 ID

    private double avgLatencyMs; // 평균 통신 지연(ms)

    private int spatSendCount; // SPaT 전송 횟수

    private int spatFailCount; // SPaT 실패 횟수

    private int connectedVehicleCount; // 접속 차량 수

}