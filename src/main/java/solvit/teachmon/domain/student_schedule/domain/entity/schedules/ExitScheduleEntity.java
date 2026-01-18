package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "exit_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExitScheduleEntity extends BaseEntity {
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private ScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exit_id")
    private ExitEntity exit;

    @Builder
    public ExitScheduleEntity(ScheduleEntity schedule, ExitEntity exit) {
        this.schedule = schedule;
        this.exit = exit;
    }
}
