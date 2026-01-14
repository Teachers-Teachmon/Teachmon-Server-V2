package solvit.teachmon.domain.supervision.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "supervision_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupervisionScheduleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @Column(name = "`day`", nullable = false)
    private LocalDate day;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false, columnDefinition = "varchar(255)")
    private SchoolPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "varchar(255)")
    private SupervisionType type;

    @Builder
    public SupervisionScheduleEntity(TeacherEntity teacher, LocalDate day, SchoolPeriod period, SupervisionType type) {
        this.teacher = teacher;
        this.day = day;
        this.period = period;
        this.type = type;
    }
}
