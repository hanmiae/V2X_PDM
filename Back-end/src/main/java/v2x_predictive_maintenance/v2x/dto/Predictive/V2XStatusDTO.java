package v2x_predictive_maintenance.v2x.dto.Predictive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2XStatusDTO {

    private String deviceId; // V2X 장비 ID

    private int spatSendCount; // SPaT 전송 횟수

    private int spatFailCount; // SPaT 실패 횟수

    private double avgLatencyMs; // 평균 지연 시간(ms)

    private int commFailCount; // 통신 실패 횟수

    private int connectedVehicleCount; // 접속 차량 수
}