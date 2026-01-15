package solvit.teachmon.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.enums.Role;
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
        this.mail = mail;
        this.name = name;
        this.profile = profile;
        this.role = Role.TEACHER;
        this.isActive = true;
        this.providerId = providerId;
        this.oAuth2Type = oAuth2Type;
    }

    public void update(String name, String profile) {
        this.name = name;
        this.profile = profile;
    }
}
