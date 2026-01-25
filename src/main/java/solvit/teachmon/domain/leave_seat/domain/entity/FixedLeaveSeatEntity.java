package solvit.teachmon.domain.leave_seat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.leave_seat.exception.FixedLeaveSeatInvalidException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.domain.exception.ScheduleChangeAccessDeniedException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Getter
@Entity
@Table(name = "fixed_leave_seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedLeaveSeatEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day", nullable = false)
    private WeekDay weekDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;

    @Column(name = "cause", nullable = false)
    private String cause;

    @OneToMany(mappedBy = "fixedLeaveSeat", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FixedLeaveSeatStudentEntity> fixedLeaveSeatStudents;

    @Builder
    public FixedLeaveSeatEntity(TeacherEntity teacher, PlaceEntity place, WeekDay weekDay, SchoolPeriod period, String cause) {
        validateTeacher(teacher);
        validatePlace(place);
        validateWeekDay(weekDay);
        validatePeriod(period);
        validateCause(cause);

        this.teacher = teacher;
        this.place = place;
        this.weekDay = weekDay;
        this.period = period;
        this.cause = cause;
    }

    private void validateTeacher(TeacherEntity teacher) {
        if(!teacher.hasStudentScheduleChangeAuthority()) {
            throw new ScheduleChangeAccessDeniedException();
        }
    }

    private void validatePlace(PlaceEntity place) {
        if(place == null) {
            throw new FixedLeaveSeatInvalidException("place(장소)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateWeekDay(WeekDay weekDay) {
        if(period == null) {
            throw new FixedLeaveSeatInvalidException("weekDay(요일)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePeriod(SchoolPeriod period) {
        if(period == null) {
            throw new FixedLeaveSeatInvalidException("period(교시)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCause(String cause) {
        if(cause == null || cause.isBlank()) {
            throw new FixedLeaveSeatInvalidException("cause(사유)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
