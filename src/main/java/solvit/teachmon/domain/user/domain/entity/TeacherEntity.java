package solvit.teachmon.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.exception.TeacherInvalidValueException;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "teacher")
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

    @Builder
    public TeacherEntity(String mail, String name, String profile) {
        this.mail = mail;
        this.name = name;
        this.profile = profile;
        this.role = Role.TEACHER;
        this.isActive = true;
    }

    public void changeRole(Role role) {
        if(role == null) {
            throw new TeacherInvalidValueException("Role 은 null 일 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        this.role = role;
    }

    public void changeName(String name) {
        if(name == null) {
            throw new TeacherInvalidValueException("name 은 null 일 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        this.name = name;
    }
}
