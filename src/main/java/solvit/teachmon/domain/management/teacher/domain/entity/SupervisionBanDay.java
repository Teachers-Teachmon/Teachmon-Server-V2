package solvit.teachmon.domain.management.teacher.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

@Getter
@Entity
@Table(name = "supervision_ban_day")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupervisionBanDay extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day", nullable = false)
    private WeekDay weekDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;
}
