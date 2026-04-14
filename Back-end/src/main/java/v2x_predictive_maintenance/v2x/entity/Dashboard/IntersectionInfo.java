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
@Table(name = "intersection_info")
public class IntersectionInfo {

    /** 교차로 ID (애플리케이션에서 할당, DB 자동 증가 아님) */
    @Id
    @Column(name = "intersection_id", nullable = false, length = 20)
    private String intersectionId;

    @Column(name = "intersection_name", nullable = false, length = 100)
    private String intersectionName;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
