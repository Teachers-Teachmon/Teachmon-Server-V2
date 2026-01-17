package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "after_school_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AfterSchoolScheduleEntity extends BaseEntity {
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private ScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "after_school_id")
    private AfterSchoolEntity afterSchool;
}
