package v2x_predictive_maintenance.v2x.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SpatLatencyDTO {

    private Double spatSuccessRate; // SPAT 성공률 (%) = (성공 / 전체) * 100

    private String status; // 우수(50ms) / 보통(100ms) / 위험(그 이상)

    private Double currentLatency; // 현재 지연(ms) = 가장 최근 평균 통신 지연 시간

    private List<Double> latencyTrend; // 시간별 latency 리스트 (최근 N개 데이터)

    /** SPAT 기준 현재 신호 단계(표시용, 최신 로그 시각 기준 순환) */
    private String currentSignal;

    /** 현재 신호 잔여 시간(초) */
    private Integer remainTimeSeconds;

    /** 패킷 송수신 상태 요약(정상/주의 등) */
    private String packetStatus;

}