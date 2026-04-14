package v2x_predictive_maintenance.v2x.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Vue Router(history) 클라이언트 경로는 서버에 파일이 없으므로 index.html로 포워드한다.
 * (/api/**, /assets/** 등은 이 매핑에 걸리지 않음)
 */
@Controller
public class SpaForwardController {

    @GetMapping({
            "/signup",
            "/main",
            "/main/**",
            "/dashboard",
            "/dashboard/**",
            "/analysis",
            "/analysis/**",
            "/V2X",
            "/V2X/**",
            "/v2x",
            "/v2x/**",
            "/Recommend",
            "/Recommend/**",
            "/recommend",
            "/recommend/**"
    })
    public String forwardSpa() {
        return "forward:/index.html";
    }
}
