package v2x_predictive_maintenance.v2x.repository.Dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import v2x_predictive_maintenance.v2x.entity.Dashboard.IntersectionInfo;

import java.util.Optional;

public interface IntersectionInfoRepository extends JpaRepository<IntersectionInfo, String> {

    Optional<IntersectionInfo> findByIntersectionId(String intersectionId);

}