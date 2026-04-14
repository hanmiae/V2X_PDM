package v2x_predictive_maintenance.v2x.controller.V2X;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import v2x_predictive_maintenance.v2x.dto.V2X.V2XHistoryDTO;
import v2x_predictive_maintenance.v2x.dto.V2X.V2XLogDTO;
import v2x_predictive_maintenance.v2x.dto.V2X.V2XSummaryDTO;
import v2x_predictive_maintenance.v2x.service.V2X.V2XService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2x")
public class V2XController {

    private final V2XService v2xService;

    // 상단 카드
    @GetMapping("/{intersectionId}")
    public V2XSummaryDTO getSummary(
            @PathVariable String intersectionId
    ) {
        return v2xService.getSummary(intersectionId);
    }

    // 그래프
    @GetMapping("/{intersectionId}/history")
    public List<V2XHistoryDTO> getHistory(
            @PathVariable String intersectionId
    ) {
        return v2xService.getHistory(intersectionId);
    }

    // 로그 테이블
    @GetMapping("/{intersectionId}/logs")
    public List<V2XLogDTO> getLogs(
            @PathVariable String intersectionId
    ) {
        return v2xService.getLogs(intersectionId);
    }
}