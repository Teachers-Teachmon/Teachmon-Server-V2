package solvit.teachmon.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.exception.InvalidTeacherInfoException;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.exception.TeacherInvalidValueException;
import solvit.teachmon.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 교사 정보를 관리하는 엔티티
 * Entity for managing teacher information
 * 
 * <p>OAuth2 인증을 통해 등록된 교사의 정보를 저장하고 관리합니다.
 * Stores and manages teacher information registered through OAuth2 authentication.
 * 
 * <p>고유 제약조건:
 * Unique constraint:
 * <ul>
 *   <li>(provider_id, oauth2_type): 동일한 OAuth2 제공자의 동일 계정은 하나만 등록 가능
 *       (provider_id, oauth2_type): Only one registration per OAuth2 provider account allowed</li>
 * </ul>
 * 
 * <p>불변 필드 (updatable=false):
 * Immutable fields (updatable=false):
 * <ul>
 *   <li>mail: OAuth2 제공자의 이메일 (OAuth2 provider email)</li>
 *   <li>providerId: OAuth2 제공자가 부여한 고유 ID (Unique ID assigned by OAuth2 provider)</li>
 *   <li>oauth2_type: OAuth2 제공자 타입 (예: GOOGLE) (OAuth2 provider type, e.g., GOOGLE)</li>
 * </ul>
 * 
 * <p>가변 필드:
 * Mutable fields:
 * <ul>
 *   <li>name: 교사 이름 (changeName 메서드로 수정) (Teacher name, modifiable via changeName)</li>
 *   <li>role: 권한 (TEACHER, ADMIN 등, changeRole 메서드로 수정) (Role like TEACHER or ADMIN, modifiable via changeRole)</li>
 *   <li>isActive: 활성화 상태 (Active status)</li>
 * </ul>
 * 
 * <p>감독 금지일:
 * Supervision ban days:
 * <ul>
 *   <li>교사가 감독할 수 없는 날짜를 관리 (Manages dates when teacher cannot supervise)</li>
 *   <li>cascade=CascadeType.REMOVE: 교사 삭제 시 관련 금지일도 함께 삭제 (Deletes associated ban days when teacher is deleted)</li>
 * </ul>
 * 
 * @see Role
 * @see OAuth2Type
 * @see SupervisionBanDayEntity
 */
@Getter
@Entity
@Table(
        name = "teacher",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider_id", "oauth2_type"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeacherEntity extends BaseEntity {
    /**
     * 이메일 (OAuth2 제공자로부터 받은 이메일, 변경 불가)
     * Email from OAuth2 provider (immutable)
     */
    @Column(name = "mail", nullable = false, updatable = false)
    private String mail;

    /** 교사 이름 (Teacher name) */
    @Column(name = "name", nullable = false)
    private String name;

    /** 프로필 이미지 URL (Profile image URL, optional) */
    @Column(name = "profile")
    private String profile;

    /** 권한 (Role: GUEST, TEACHER, ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * 활성화 상태
     * Active status
     * 
     * <p>false인 경우: 퇴직, 휴직 등으로 시스템 사용 불가
     * If false: Cannot use system due to retirement, leave of absence, etc.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * OAuth2 제공자가 부여한 고유 ID (변경 불가)
     * Unique ID assigned by OAuth2 provider (immutable)
     * 
     * <p>Google의 경우: Google User ID
     * For Google: Google User ID
     */
    @Column(name = "provider_id", nullable = false, updatable = false)
    private String providerId;

    /**
     * OAuth2 인증 제공자 타입 (변경 불가)
     * OAuth2 authentication provider type (immutable)
     * 
     * <p>현재 지원: GOOGLE
     * Currently supported: GOOGLE
     */
    @Column(name = "oauth2_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private OAuth2Type oAuth2Type;
  
    /**
     * 감독 금지일 목록
     * List of supervision ban days
     * 
     * <p>교사가 감독할 수 없는 날짜를 관리합니다 (개인 사정, 출장 등).
     * Manages dates when teacher cannot supervise (personal reasons, business trips, etc.).
     * 
     * <p>cascade=CascadeType.REMOVE: 교사 삭제 시 관련 금지일도 함께 삭제
     * cascade=CascadeType.REMOVE: Deletes associated ban days when teacher is deleted
     */
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SupervisionBanDayEntity> supervisionBanDays = new ArrayList<>();

    /**
     * 교사 엔티티 생성자
     * Teacher entity constructor
     * 
     * <p>새 교사 등록 시 기본값:
     * Default values for new teacher:
     * <ul>
     *   <li>role: TEACHER (기본 교사 권한) (Default teacher role)</li>
     *   <li>isActive: true (활성화 상태) (Active status)</li>
     * </ul>
     * 
     * @param mail OAuth2 이메일 (OAuth2 email, must not be null or empty)
     * @param name 교사 이름 (Teacher name, must not be null or empty)
     * @param profile 프로필 이미지 URL (Profile image URL, optional)
     * @param providerId OAuth2 제공자 ID (OAuth2 provider ID, must not be null or empty)
     * @param oAuth2Type OAuth2 제공자 타입 (OAuth2 provider type, must not be null)
     * @throws InvalidTeacherInfoException 유효성 검증 실패 시 (If validation fails)
     */
    @Builder
    public TeacherEntity(String mail, String name, String profile, String providerId, OAuth2Type oAuth2Type) {
        validateMail(mail);
        validateProviderId(providerId);
        validateOAuth2Type(oAuth2Type);
        validateNameFiled(name);

        this.mail = mail;
        this.name = name;
        this.profile = profile;
        this.role = Role.TEACHER;
        this.isActive = true;
        this.providerId = providerId;
        this.oAuth2Type = oAuth2Type;
    }

    private void validateMail(String mail) {
        if(mail == null || mail.trim().isEmpty())
            throw new InvalidTeacherInfoException("메일은 비어 있을 수 없습니다.");
    }

    private void validateProviderId(String providerId) {
        if(providerId == null || providerId.trim().isEmpty())
            throw new InvalidTeacherInfoException("Provider 아이디는 비어 있을 수 없습니다.");
    }

    private void validateOAuth2Type(OAuth2Type oAuth2Type) {
        if(oAuth2Type == null)
            throw new InvalidTeacherInfoException("OAuth2 타입은 비어 있을 수 없습니다.");
    }

    private void validateNameFiled(String name) {
        if(name == null || name.trim().isEmpty())
            throw new InvalidTeacherInfoException("이름은 비어 있을 수 없습니다.");
    }

    /**
     * 교사 권한 변경
     * Changes teacher role
     * 
     * <p>사용 사례:
     * Use cases:
     * <ul>
     *   <li>일반 교사를 관리자로 승격 (TEACHER → ADMIN) (Promote teacher to admin)</li>
     *   <li>관리자 권한 회수 (ADMIN → TEACHER) (Revoke admin privileges)</li>
     * </ul>
     * 
     * @param role 새 권한 (New role, must not be null)
     * @throws TeacherInvalidValueException role이 null인 경우 (If role is null)
     */
    public void changeRole(Role role) {
        if(role == null) {
            throw new TeacherInvalidValueException("role(권한)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
        this.role = role;
    }

    /**
     * 교사 이름 변경
     * Changes teacher name
     * 
     * <p>결혼, 개명 등으로 이름이 변경된 경우 사용합니다.
     * Used when name changes due to marriage, legal name change, etc.
     * 
     * @param name 새 이름 (New name, must not be null)
     * @throws TeacherInvalidValueException name이 null인 경우 (If name is null)
     */
    public void changeName(String name) {
        if(name == null) {
            throw new TeacherInvalidValueException("name(이름)은 필수입니다", HttpStatus.BAD_REQUEST);
        }
        this.name = name;
    }
}
