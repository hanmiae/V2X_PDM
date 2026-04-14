package v2x_predictive_maintenance.v2x.service.Recommend;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import v2x_predictive_maintenance.v2x.dto.Recommend.RecommendItemDTO;
import v2x_predictive_maintenance.v2x.dto.Recommend.RecommendSummaryDTO;
import v2x_predictive_maintenance.v2x.entity.Dashboard.IntersectionInfo;
import v2x_predictive_maintenance.v2x.entity.Dashboard.RiskAnalysisResult;
import v2x_predictive_maintenance.v2x.repository.Dashboard.IntersectionInfoRepository;
import v2x_predictive_maintenance.v2x.repository.Dashboard.RiskAnalysisResultRepository;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RiskAnalysisResultRepository riskAnalysisResultRepository;
    private final IntersectionInfoRepository intersectionInfoRepository;

    // 상단 카드
    public RecommendSummaryDTO getSummary() {

        long critical = riskAnalysisResultRepository.countByRiskLevel("위험");
        long warning = riskAnalysisResultRepository.countByRiskLevel("주의")
                + riskAnalysisResultRepository.countByRiskLevel("경고");
        long normal = riskAnalysisResultRepository.countByRiskLevel("정상");

        return new RecommendSummaryDTO(
                critical,
                warning,
                normal
        );
    }

    // 테이블 목록 (페이징)
    public Page<RecommendItemDTO> getRecommendList(Pageable pageable) {
        return riskAnalysisResultRepository.findAll(pageable).map(this::convertToDTO);
    }

    public byte[] buildReportFile(Long riskResultId) {
        RiskAnalysisResult r = riskAnalysisResultRepository.findById(riskResultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "분석 결과를 찾을 수 없습니다."));
        RecommendItemDTO dto = convertToDTO(r);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("장비 유지보수 및 교체 권고 보고서").append(nl);
        sb.append("========================================").append(nl).append(nl);
        sb.append("출력 일시: ").append(fmt.format(java.time.LocalDateTime.now())).append(nl);
        sb.append("분석 일시: ").append(dto.getAnalysisTime()).append(nl).append(nl);
        sb.append("[교차로]").append(nl);
        sb.append("  ID: ").append(dto.getIntersectionId()).append(nl);
        sb.append("  명칭: ").append(dto.getIntersectionName()).append(nl).append(nl);
        sb.append("[장비]").append(nl);
        sb.append("  장비 ID: ").append(dto.getDeviceId()).append(nl);
        sb.append("  유형: ").append(dto.getDeviceType()).append(nl).append(nl);
        sb.append("[위험 평가]").append(nl);
        sb.append("  등급: ").append(dto.getRiskLevel()).append(nl);
        sb.append("  예상 잔여 수명(일): ").append(Math.round(dto.getRemainDays())).append(nl).append(nl);
        sb.append("[AI 분석]").append(nl);
        sb.append(dto.getAnalysisComment()).append(nl).append(nl);
        sb.append("[현장 조치 권고]").append(nl);
        sb.append(dto.getActionMessage()).append(nl);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private RecommendItemDTO convertToDTO(RiskAnalysisResult r) {

        String intersectionName = intersectionInfoRepository
                .findByIntersectionId(r.getIntersectionId())
                .map(IntersectionInfo::getIntersectionName)
                .orElse("교차로명 없음");

        String deviceType =
                r.getControllerRiskScore().doubleValue() >
                        r.getV2xRiskScore().doubleValue()
                        ? "신호 제어기"
                        : "V2X RSU";

        String deviceId =
                deviceType.equals("신호 제어기")
                        ? "CTRL-" + r.getIntersectionId().substring(4)
                        : "V2X-RSE-" + r.getIntersectionId().substring(4);

        String riskClass = switch (r.getRiskLevel()) {
            case "위험" -> "danger";
            case "주의", "경고" -> "warning";
            default -> "normal";
        };

        double remainDays =
                100 - r.getTotalRiskScore().doubleValue();

        String actionMessage = switch (r.getRiskLevel()) {
            case "위험" -> "즉시 현장 출동 및 부품 교체 필요";
            case "경고" -> "조기 점검 및 모니터링 강화 권고";
            case "주의" -> "정밀 점검 및 원격 진단 권고";
            default -> "정기 점검 유지";
        };

        return new RecommendItemDTO(
                r.getRiskResultId(),
                r.getIntersectionId(),
                intersectionName,
                deviceId,
                deviceType,
                r.getRiskLevel(),
                riskClass,
                r.getAnalysisComment(),
                remainDays,
                r.getAnalysisTime().toString(),
                actionMessage
        );
    }
}