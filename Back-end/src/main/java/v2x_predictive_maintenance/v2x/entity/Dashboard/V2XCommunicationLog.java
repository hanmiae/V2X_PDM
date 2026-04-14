package v2x_predictive_maintenance.v2x.entity.Dashboard;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "v2x_communication_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class V2XCommunicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "communication_log_id")
    private Long communicationLogId;

    @Column(name = "intersection_id", nullable = false, length = 20)
    private String intersectionId;

    @Column(name = "v2x_device_id", nullable = false, length = 50)
    private String v2xDeviceId;

    @Column(name = "v2x_log_time", nullable = false)
    private LocalDateTime v2xLogTime;

    @Column(name = "spat_send_count", nullable = false)
    private Integer spatSendCount;

    @Column(name = "spat_fail_count", nullable = false)
    private Integer spatFailCount;

    @Column(name = "avg_latency_ms", precision = 8, scale = 2)
    private BigDecimal avgLatencyMs;

    @Column(name = "comm_fail_count", nullable = false)
    private Integer commFailCount;

    @Column(name = "connected_vehicle_count", nullable = false)
    private Integer connectedVehicleCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
