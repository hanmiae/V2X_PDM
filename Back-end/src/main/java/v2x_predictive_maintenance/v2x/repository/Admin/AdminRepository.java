package v2x_predictive_maintenance.v2x.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import v2x_predictive_maintenance.v2x.entity.Admin.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {

    //로그인용 : 아이디로 관리자 찾기
    Optional<Admin> findByLoginId(String loginId);

    //회원가입용 : 아이디 중복 체크
    boolean existsByLoginId(String loginId);

    // 회원가입용 : 사원번호 중복 체크
    boolean existsByEmployeeNo(String employeeNo);

}
