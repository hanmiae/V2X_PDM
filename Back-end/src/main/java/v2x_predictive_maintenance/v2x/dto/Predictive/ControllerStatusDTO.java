package v2x_predictive_maintenance.v2x.dto.Predictive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControllerStatusDTO {

    private String deviceId; // 제어기 장비 ID

    private double cpuTemp; // CPU 온도

    private double responseTimeMs; // 응답 속도(ms)

    private int uptimeMin; // 가동 시간(분)

    private int rebootCount; // 재부팅 횟수

    private int errorCount; // 오류 횟수
}