package solvit.teachmon.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.exception.InvalidTeacherInfoException;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(
        name = "teacher",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider_id", "oauth2_type"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeacherEntity extends BaseEntity {
    @Column(name = "mail", nullable = false, updatable = false)
    private String mail;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile")
    private String profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "provider_id", nullable = false, updatable = false)
    private String providerId;

    @Column(name = "oauth2_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private OAuth2Type oAuth2Type;

    @Builder
    public TeacherEntity(String mail, String name, String profile, String providerId, OAuth2Type oAuth2Type) {
        validateFields(mail, name, providerId, oAuth2Type);

        this.mail = mail;
        this.name = name;
        this.profile = profile;
        this.role = Role.TEACHER;
        this.isActive = true;
        this.providerId = providerId;
        this.oAuth2Type = oAuth2Type;
    }

    private void validateFields(String mail, String name, String providerId, OAuth2Type oAuth2Type) {
        if(mail == null || mail.trim().isEmpty())
            throw new InvalidTeacherInfoException("메일은 비어 있을 수 없습니다.");
        if(name == null || name.trim().isEmpty())
            throw new InvalidTeacherInfoException("이름은 비어 있을 수 없습니다.");
        if(providerId == null || providerId.trim().isEmpty())
            throw new InvalidTeacherInfoException("Provider 아이디는 비어 있을 수 없습니다.");
        if(oAuth2Type == null)
            throw new InvalidTeacherInfoException("OAuth2 타입은 비어 있을 수 없습니다.");
    }
}
