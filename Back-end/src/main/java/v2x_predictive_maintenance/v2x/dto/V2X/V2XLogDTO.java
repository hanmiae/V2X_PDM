package v2x_predictive_maintenance.v2x.dto.V2X;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2XLogDTO {

    private String v2xLogTime; // 측정 시각

    private int spatSendCount; // SPaT 전송 횟수

    private int spatFailCount; // SPaT 실패 횟수

    private double avgLatencyMs; // 평균 지연(ms)

    private int commFailCount; // 통신 실패 횟수

    private int connectedVehicleCount; // 접속 차량 수
}