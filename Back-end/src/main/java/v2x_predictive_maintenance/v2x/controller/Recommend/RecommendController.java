package v2x_predictive_maintenance.v2x.controller.Recommend;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v2x_predictive_maintenance.v2x.dto.Recommend.RecommendItemDTO;
import v2x_predictive_maintenance.v2x.dto.Recommend.RecommendSummaryDTO;
import v2x_predictive_maintenance.v2x.service.Recommend.RecommendService;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    // 상단 카드
    @GetMapping("/summary")
    public RecommendSummaryDTO getSummary() {
        return recommendService.getSummary();
    }

    // 테이블 목록 (페이징: page, size)
    @GetMapping("/list")
    public Page<RecommendItemDTO> getRecommendList(
            @PageableDefault(size = 5, sort = "analysisTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return recommendService.getRecommendList(pageable);
    }

    @GetMapping("/report/{riskResultId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long riskResultId) {
        byte[] body = recommendService.buildReportFile(riskResultId);
        String filename = "maint-report-" + riskResultId + ".txt";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .body(body);
    }
}