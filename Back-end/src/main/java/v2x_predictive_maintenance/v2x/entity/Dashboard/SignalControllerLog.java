package v2x_predictive_maintenance.v2x.entity.Dashboard;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "signal_controller_log")
@Getter
@Setter
@NoArgsConstructor
public class SignalControllerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "controller_log_id")
    private Long controllerLogId;

    @Column(name = "intersection_id", nullable = false, length = 20)
    private String intersectionId;

    @Column(name = "controller_device_id", nullable = false, length = 50)
    private String controllerDeviceId;

    @Column(name = "controller_log_time", nullable = false)
    private LocalDateTime controllerLogTime;

    @Column(name = "response_time_ms", precision = 8, scale = 2)
    private BigDecimal responseTimeMs;

    @Column(name = "error_count", nullable = false)
    private Integer errorCount;

    @Column(name = "reboot_count", nullable = false)
    private Integer rebootCount;

    @Column(name = "cpu_temp", precision = 6, scale = 2)
    private BigDecimal cpuTemp;

    @Column(name = "uptime_min")
    private Integer uptimeMin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
