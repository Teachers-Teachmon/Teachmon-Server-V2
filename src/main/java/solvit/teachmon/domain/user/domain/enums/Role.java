package solvit.teachmon.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 역할을 나타내는 열거형
 * Enumeration representing user roles
 * 
 * <p>Spring Security의 권한 체계와 통합되어 사용됩니다.
 * Integrated with Spring Security's authorization system.
 * 
 * <p>역할 계층 (낮음 → 높음):
 * Role hierarchy (low → high):
 * <ul>
 *   <li>GUEST: OAuth2 인증은 성공했으나 시스템에 등록되지 않은 사용자 (OAuth2 authenticated but not registered in system)</li>
 *   <li>TEACHER: 일반 교사 권한 (학생 관리, 감독 일정 등) (Regular teacher permissions - student management, supervision, etc.)</li>
 *   <li>ADMIN: 관리자 권한 (교사 관리, 시스템 설정 등) (Administrator permissions - teacher management, system settings, etc.)</li>
 * </ul>
 * 
 * <p>GUEST 역할 사용 이유:
 * Why GUEST role exists:
 * <ul>
 *   <li>OAuth2 인증 성공 후 신규 사용자를 자동 등록하지 않음 (Does not auto-register new users after OAuth2 authentication)</li>
 *   <li>ADMIN이 명시적으로 교사를 등록해야 TEACHER 역할 부여 (ADMIN must explicitly register teachers to grant TEACHER role)</li>
 *   <li>보안: 무분별한 접근 방지 (Security: Prevents unauthorized access)</li>
 * </ul>
 * 
 * @see solvit.teachmon.domain.user.domain.entity.TeacherEntity
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    /** 게스트 (미등록 사용자) - Guest (unregistered user) */
    GUEST("ROLE_GUEST"),
    /** 교사 - Teacher */
    TEACHER("ROLE_TEACHER"),
    /** 관리자 - Administrator */
    ADMIN("ROLE_ADMIN");

    /** Spring Security용 역할 문자열 (Role string for Spring Security) */
    private final String value;
}