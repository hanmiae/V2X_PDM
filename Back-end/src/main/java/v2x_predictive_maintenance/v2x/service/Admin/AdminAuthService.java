package v2x_predictive_maintenance.v2x.service.Admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import v2x_predictive_maintenance.v2x.dto.Admin.AdminLoginDTO;
import v2x_predictive_maintenance.v2x.dto.Admin.AdminSignupDTO;
import v2x_predictive_maintenance.v2x.entity.Admin.Admin;
import v2x_predictive_maintenance.v2x.repository.Admin.AdminRepository;

@Service
@RequiredArgsConstructor

public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public String signup(AdminSignupDTO dto){

        // 아이디 중복 검사
        if(adminRepository.existsByLoginId(dto.getLoginId())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 사원번호 중복 검사
        if (adminRepository.existsByEmployeeNo(dto.getEmployeeNo())) {
            throw new IllegalArgumentException("이미 등록된 사원번호입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        // DTO → Entity 변환
        Admin admin = Admin.builder()
                .employeeNo(dto.getEmployeeNo())
                .loginId(dto.getLoginId())
                .password(encodedPassword) // 암호화된 값 저장
                .adminName(dto.getAdminName())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        // 저장
        adminRepository.save(admin);

        return "회원가입이 완료되었습니다.";
    }

    // 로그인
    public String login(AdminLoginDTO dto) {

        // 아이디 존재 여부 확인
        Admin admin = adminRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return "로그인 성공";
    }
}
