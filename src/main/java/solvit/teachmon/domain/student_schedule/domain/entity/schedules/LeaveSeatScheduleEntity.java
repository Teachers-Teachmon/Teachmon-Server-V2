package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.exception.LeaveSeatScheduleValueInvalidException;
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

    @Builder
    public LeaveSeatScheduleEntity(ScheduleEntity schedule, LeaveSeatEntity leaveSeat) {
        this.schedule = schedule;
        this.leaveSeat = leaveSeat;
    }

    private void validateSchedule(ScheduleEntity schedule) {
        if(schedule == null) {
            throw new LeaveSeatScheduleValueInvalidException("schedule(스케줄)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
        else if(!schedule.getType().equals(ScheduleType.LEAVE_SEAT)) {
            throw new LeaveSeatScheduleValueInvalidException("schedule(스케줄)의 타입은 이석이여야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateLeaveSeat(LeaveSeatEntity leaveSeat) {
        if(leaveSeat == null) {
            throw new LeaveSeatScheduleValueInvalidException("leaveSeat(이석)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
