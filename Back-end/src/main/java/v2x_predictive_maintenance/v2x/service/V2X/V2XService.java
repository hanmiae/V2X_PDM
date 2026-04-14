package v2x_predictive_maintenance.v2x.service.V2X;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v2x_predictive_maintenance.v2x.dto.V2X.V2XHistoryDTO;
import v2x_predictive_maintenance.v2x.dto.V2X.V2XLogDTO;
import v2x_predictive_maintenance.v2x.dto.V2X.V2XSummaryDTO;
import v2x_predictive_maintenance.v2x.entity.Dashboard.V2XCommunicationLog;
import v2x_predictive_maintenance.v2x.repository.Dashboard.V2XCommunicationLogRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class V2XService {

    private final V2XCommunicationLogRepository v2xCommunicationLogRepository;

    // 상단 카드 조회
    public V2XSummaryDTO getSummary(String intersectionId) {

        V2XCommunicationLog result = v2xCommunicationLogRepository
                .findTopByIntersectionIdOrderByV2xLogTimeDesc(intersectionId)
                .orElseThrow(() -> new RuntimeException("V2X 데이터 없음"));

        return new V2XSummaryDTO(
                result.getV2xDeviceId(),
                result.getAvgLatencyMs().doubleValue(),
                result.getSpatSendCount(),
                result.getSpatFailCount(),
                result.getConnectedVehicleCount()
        );
    }

    // 그래프 조회
    public List<V2XHistoryDTO> getHistory(String intersectionId) {

        List<V2XCommunicationLog> results =
                v2xCommunicationLogRepository
                        .findTop7ByIntersectionIdOrderByV2xLogTimeAsc(intersectionId);

        return results.stream()
                .map(log -> new V2XHistoryDTO(
                        log.getV2xLogTime().toLocalTime().toString().substring(0, 5),
                        log.getAvgLatencyMs().doubleValue()
                ))
                .toList();
    }

    // 로그 테이블 조회
    public List<V2XLogDTO> getLogs(String intersectionId) {

        List<V2XCommunicationLog> results =
                v2xCommunicationLogRepository
                        .findByIntersectionIdOrderByV2xLogTimeDesc(intersectionId);

        return results.stream()
                .map(log -> new V2XLogDTO(
                        log.getV2xLogTime().toString(),
                        log.getSpatSendCount(),
                        log.getSpatFailCount(),
                        log.getAvgLatencyMs().doubleValue(),
                        log.getCommFailCount(),
                        log.getConnectedVehicleCount()
                ))
                .toList();
    }
}