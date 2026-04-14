package v2x_predictive_maintenance.v2x.entity.Admin;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_user")
@Builder

public class Admin {

    @Id
    @Column(name = "employee_no", nullable = false, length = 30)
    private String employeeNo; // 사원번호(pk)

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId; // 아이디

    @Column(name = "password", nullable = false, length = 255)
    private String password; // 비밀번호

    @Column(name = "admin_name", nullable = false, length = 100)
    private String adminName; // 이름

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber; //휴대폰번호

}