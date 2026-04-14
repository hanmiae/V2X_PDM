package v2x_predictive_maintenance.v2x.entity.Dashboard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "risk_analysis_result")
public class RiskAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_result_id")
    private Long riskResultId;

    @Column(name = "intersection_id", nullable = false, length = 20)
    private String intersectionId;

    @Column(name = "controller_log_id", nullable = false)
    private Long controllerLogId;

    @Column(name = "communication_log_id", nullable = false)
    private Long communicationLogId;

    @Column(name = "analysis_time", nullable = false)
    private LocalDateTime analysisTime;

    @Column(name = "controller_risk_score", precision = 6, scale = 2)
    private BigDecimal controllerRiskScore;

    @Column(name = "v2x_risk_score", precision = 6, scale = 2)
    private BigDecimal v2xRiskScore;

    @Column(name = "total_risk_score", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalRiskScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "analysis_comment", columnDefinition = "text")
    private String analysisComment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
