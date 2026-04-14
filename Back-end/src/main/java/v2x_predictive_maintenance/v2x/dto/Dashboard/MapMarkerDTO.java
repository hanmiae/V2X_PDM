package v2x_predictive_maintenance.v2x.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MapMarkerDTO {

    private String id;      // 교차로 ID
    private String name;    // 교차로 이름
    private String status;  // normal / error

    private Double lat;     // 위도
    private Double lng;     // 경도
}