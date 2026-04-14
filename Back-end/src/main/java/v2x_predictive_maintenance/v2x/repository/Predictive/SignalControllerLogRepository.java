package v2x_predictive_maintenance.v2x.repository.Predictive;

import org.springframework.data.jpa.repository.JpaRepository;
import v2x_predictive_maintenance.v2x.entity.Dashboard.SignalControllerLog;

import java.util.Optional;

public interface SignalControllerLogRepository extends JpaRepository<SignalControllerLog, Long> {

    // 특정 교차로의 최신 제어기 상태 1개 조회
    Optional<SignalControllerLog>
    findTopByIntersectionIdOrderByControllerLogTimeDesc(String intersectionId);
}