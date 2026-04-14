package v2x_predictive_maintenance.v2x.controller.Admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v2x_predictive_maintenance.v2x.dto.Admin.AdminLoginDTO;
import v2x_predictive_maintenance.v2x.dto.Admin.AdminSignupDTO;
import v2x_predictive_maintenance.v2x.service.Admin.AdminAuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody AdminSignupDTO dto) {
        String message = adminAuthService.signup(dto);
        return ResponseEntity.ok(Map.of("message", message));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AdminLoginDTO dto) {
        String message = adminAuthService.login(dto);
        return ResponseEntity.ok(Map.of("message", message));
    }

}