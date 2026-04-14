package v2x_predictive_maintenance.v2x.service.Predictive;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v2x_predictive_maintenance.v2x.dto.Predictive.ControllerStatusDTO;
import v2x_predictive_maintenance.v2x.dto.Predictive.V2XStatusDTO;
import v2x_predictive_maintenance.v2x.entity.Dashboard.SignalControllerLog;
import v2x_predictive_maintenance.v2x.entity.Dashboard.V2XCommunicationLog;
import v2x_predictive_maintenance.v2x.repository.Dashboard.V2XCommunicationLogRepository;
import v2x_predictive_maintenance.v2x.repository.Predictive.SignalControllerLogRepository;

@Service
@RequiredArgsConstructor
public class PredictiveStatusService {

    private final SignalControllerLogRepository signalControllerLogRepository;
    private final V2XCommunicationLogRepository v2xCommunicationLogRepository;

    // 제어기 상태 조회
    public ControllerStatusDTO getControllerStatus(String intersectionId) {

        SignalControllerLog result = signalControllerLogRepository
                .findTopByIntersectionIdOrderByControllerLogTimeDesc(intersectionId)
                .orElseThrow(() -> new RuntimeException("제어기 데이터 없음"));

        return new ControllerStatusDTO(
                result.getControllerDeviceId(),                 // 장비 ID
                result.getCpuTemp().doubleValue(),             // CPU 온도
                result.getResponseTimeMs().doubleValue(),      // 응답 속도
                result.getUptimeMin(),                         // 가동 시간
                result.getRebootCount(),                       // 재부팅 횟수
                result.getErrorCount()                         // 오류 횟수
        );
    }

    // V2X 상태 조회
    public V2XStatusDTO getV2XStatus(String intersectionId) {

        V2XCommunicationLog result = v2xCommunicationLogRepository
                .findTopByIntersectionIdOrderByV2xLogTimeDesc(intersectionId)
                .orElseThrow(() -> new RuntimeException("V2X 데이터 없음"));

        return new V2XStatusDTO(
                result.getV2xDeviceId(),                      // 장비 ID
                result.getSpatSendCount(),                    // 전송 횟수
                result.getSpatFailCount(),                    // 실패 횟수
                result.getAvgLatencyMs().doubleValue(),       // 평균 지연
                result.getCommFailCount(),                    // 통신 실패
                result.getConnectedVehicleCount()             // 접속 차량
        );
    }
}