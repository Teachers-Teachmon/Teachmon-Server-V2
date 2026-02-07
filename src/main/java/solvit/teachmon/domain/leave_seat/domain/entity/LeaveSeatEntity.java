package solvit.teachmon.domain.leave_seat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.leave_seat.exception.LeaveSeatValueInvalidException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.LeaveSeatScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.exception.ScheduleChangeAccessDeniedException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@Table(name = "leave_seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveSeatEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;

    @Column(name = "cause", nullable = false)
    private String cause;

    @OneToMany(mappedBy = "leaveSeat", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LeaveSeatScheduleEntity> leaveSeatSchedules = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "leaveSeat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveSeatStudentEntity> leaveSeatStudents = new java.util.ArrayList<>();

    @Builder
    public LeaveSeatEntity(TeacherEntity teacher, PlaceEntity place, LocalDate day, SchoolPeriod period, String cause) {
        validateTeacher(teacher);
        validatePlace(place);
        validateDay(day);
        validatePeriod(period);
        validateCause(cause);

        this.teacher = teacher;
        this.place = place;
        this.day = day;
        this.period = period;
        this.cause = cause;
    }

    public void changeLeaveSeatInfo(TeacherEntity teacher, PlaceEntity place, LocalDate day, SchoolPeriod period, String cause) {
        validateTeacher(teacher);
        validatePlace(place);
        validateDay(day);
        validatePeriod(period);
        validateCause(cause);

        this.teacher = teacher;
        this.place = place;
        this.day = day;
        this.period = period;
        this.cause = cause;
    }

    public static LeaveSeatEntity from(FixedLeaveSeatEntity fl, LocalDate day) {
        return LeaveSeatEntity.builder()
                .teacher(fl.getTeacher())
                .place(fl.getPlace())
                .day(day)
                .period(fl.getPeriod())
                .cause(fl.getCause())
                .build();
    }

    public void addLeaveSeatStudent(LeaveSeatStudentEntity leaveSeatStudent) {
        this.leaveSeatStudents.add(leaveSeatStudent);
    }

    private void validateTeacher(TeacherEntity teacher) {
        if(!teacher.hasStudentScheduleChangeAuthority()) {
            throw new ScheduleChangeAccessDeniedException();
        }
    }

    private void validatePlace(PlaceEntity place) {
        if(place == null) {
            throw new LeaveSeatValueInvalidException("place(장소)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateDay(LocalDate day) {
        if(day == null) {
            throw new LeaveSeatValueInvalidException("day(날짜)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
        else if(day.isBefore(LocalDate.now())) {
            throw new LeaveSeatValueInvalidException("day(날짜)는 오늘 이후여야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePeriod(SchoolPeriod period) {
        if(period == null) {
            throw new LeaveSeatValueInvalidException("period(교시)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCause(String cause) {
        if(cause == null || cause.isBlank()) {
            throw new LeaveSeatValueInvalidException("cause(사유)는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
