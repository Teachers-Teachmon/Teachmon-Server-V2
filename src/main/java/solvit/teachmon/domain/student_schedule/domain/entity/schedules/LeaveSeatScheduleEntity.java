package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "leave_seat_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveSeatScheduleEntity extends BaseEntity {
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    private ScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_seat_id")
    private LeaveSeatEntity leaveSeat;
}
