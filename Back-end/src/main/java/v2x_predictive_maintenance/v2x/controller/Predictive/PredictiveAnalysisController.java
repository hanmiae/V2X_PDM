package v2x_predictive_maintenance.v2x.controller.Predictive;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import v2x_predictive_maintenance.v2x.dto.Predictive.ControllerStatusDTO;
import v2x_predictive_maintenance.v2x.dto.Predictive.PredictiveAnalysisDTO;
import v2x_predictive_maintenance.v2x.dto.Predictive.RiskHistoryDTO;
import v2x_predictive_maintenance.v2x.dto.Predictive.V2XStatusDTO;
import v2x_predictive_maintenance.v2x.service.Predictive.PredictiveAnalysisService;
import v2x_predictive_maintenance.v2x.service.Predictive.PredictiveStatusService;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/predictive")
public class PredictiveAnalysisController {

    private final PredictiveAnalysisService predictiveAnalysisService;
    private final PredictiveStatusService predictiveStatusService;

    // 예지보전 분석 조회 API
    @GetMapping("/{intersectionId}")
    public PredictiveAnalysisDTO getPredictiveAnalysis(
            @PathVariable String intersectionId // 교차로 ID
    ) {
        return predictiveAnalysisService.getPredictiveAnalysis(intersectionId);
    }

    // 최근 위험 점수 히스토리 조회 (그래프용)
    @GetMapping("/{intersectionId}/history")
    public List<RiskHistoryDTO> getRiskHistory(
            @PathVariable String intersectionId
    ) {
        return predictiveAnalysisService.getRiskHistory(intersectionId);
    }

    // 제어기 상태 조회 API (하단 왼쪽 카드용)
    @GetMapping("/{intersectionId}/controller")
    public ControllerStatusDTO getControllerStatus(
            @PathVariable String intersectionId
    ) {
        return predictiveStatusService.getControllerStatus(intersectionId);
    }

    // V2X 상태 조회 API
    @GetMapping("/{intersectionId}/v2x")
    public V2XStatusDTO getV2XStatus(
            @PathVariable String intersectionId
    ) {
        return predictiveStatusService.getV2XStatus(intersectionId);
    }

}