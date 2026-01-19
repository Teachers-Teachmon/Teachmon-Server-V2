package solvit.teachmon.domain.management.teacher.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.management.teacher.exception.InvalidSupervisionBanDayInfoException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.WeekDay;

@Getter
@Entity
@Table(name = "supervision_ban_day")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupervisionBanDayEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day", nullable = false)
    private WeekDay weekDay;

    @Builder
    public SupervisionBanDayEntity(TeacherEntity teacher, WeekDay weekDay) {
        validateTeacher(teacher);
        validateWeekDay(weekDay);

        this.teacher = teacher;
        this.weekDay = weekDay;
    }

    private void validateTeacher(TeacherEntity teacher) {
        if (teacher == null) {
            throw new InvalidSupervisionBanDayInfoException("grade(학년)는 1 ~ 3 사이여야 합니다.");
        }
    }

    private void validateWeekDay(WeekDay weekDay) {
        if (weekDay == null) {
            throw new InvalidSupervisionBanDayInfoException("weekDay(요일)는 필수입니다.");
        }
    }
}
