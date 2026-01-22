package solvit.teachmon.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.exception.TeacherInvalidValueException;
import solvit.teachmon.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "teacher")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeacherEntity extends BaseEntity {
    private static final List<Role> STUDENT_SCHEDULE_CHANGE_AUTHORITIES = List.of(
            Role.TEACHER,
            Role.ADMIN
    );

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

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SupervisionBanDayEntity> supervisionBanDays = new ArrayList<>();

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
            throw new TeacherInvalidValueException("role(권한)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
        this.role = role;
    }

    public void changeName(String name) {
        if(name == null) {
            throw new TeacherInvalidValueException("name(이름)은 필수입니다", HttpStatus.BAD_REQUEST);
        }
        this.name = name;
    }

    public Boolean hasStudentScheduleChangeAuthority() {
        return this.role.isContains(STUDENT_SCHEDULE_CHANGE_AUTHORITIES);
    }
}
