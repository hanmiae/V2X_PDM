package v2x_predictive_maintenance.v2x.service.Dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v2x_predictive_maintenance.v2x.dto.Dashboard.MapMarkerDTO;
import v2x_predictive_maintenance.v2x.entity.Dashboard.IntersectionInfo;
import v2x_predictive_maintenance.v2x.entity.Dashboard.RiskAnalysisResult;
import v2x_predictive_maintenance.v2x.repository.Dashboard.IntersectionInfoRepository;
import v2x_predictive_maintenance.v2x.repository.Dashboard.RiskAnalysisResultRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapService {

    private final IntersectionInfoRepository intersectionInfoRepository;
    private final RiskAnalysisResultRepository riskAnalysisResultRepository;

    /** 지도 마커 — {@link DashboardService} 요약과 동일한 최신 위험 스냅샷 사용 */
    public List<MapMarkerDTO> getMapMarkers() {
        Map<String, RiskAnalysisResult> latestById = new HashMap<>();
        for (RiskAnalysisResult r : riskAnalysisResultRepository.findLatestRowPerIntersection()) {
            if (r.getIntersectionId() != null && !r.getIntersectionId().isBlank()) {
                latestById.put(r.getIntersectionId().trim(), r);
            }
        }

        List<IntersectionInfo> intersections = intersectionInfoRepository.findAll();

        return intersections.stream()
                .map(
                        intersection -> {
                            String iid =
                                    intersection.getIntersectionId() == null
                                            ? ""
                                            : intersection.getIntersectionId().trim();
                            RiskAnalysisResult row = latestById.get(iid);
                            boolean isNormal =
                                    row == null
                                            || MonitoringRiskLevel.isNormalLatest(
                                                    MonitoringRiskLevel.normalize(row.getRiskLevel()));
                            String status = isNormal ? "normal" : "error";

                            return MapMarkerDTO.builder()
                                    .id(intersection.getIntersectionId())
                                    .name(intersection.getIntersectionName())
                                    .lat(intersection.getLatitude().doubleValue())
                                    .lng(intersection.getLongitude().doubleValue())
                                    .status(status)
                                    .build();
                        })
                .toList();
    }
}
