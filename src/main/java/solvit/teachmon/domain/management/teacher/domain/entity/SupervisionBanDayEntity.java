package solvit.teachmon.domain.management.teacher.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
        if (teacher == null) {
            throw new IllegalArgumentException("teacher는 필수입니다");
        }
        if (weekDay == null) {
            throw new IllegalArgumentException("weekDay는 필수입니다");
        }
        this.teacher = teacher;
        this.weekDay = weekDay;
    }
}
