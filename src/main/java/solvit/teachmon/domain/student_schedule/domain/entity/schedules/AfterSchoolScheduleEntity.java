package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.exception.AfterSchoolScheduleValueInvalidException;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "after_school_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AfterSchoolScheduleEntity extends BaseEntity {
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    private ScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "after_school_id")
    private AfterSchoolEntity afterSchool;

    @Builder
    public AfterSchoolScheduleEntity(ScheduleEntity schedule, AfterSchoolEntity afterSchool) {
        validateSchedule(schedule);
        validateAfterSchool(afterSchool);

        this.schedule = schedule;
        this.afterSchool = afterSchool;
    }

    private void validateSchedule(ScheduleEntity schedule) {
        if(schedule == null) {
            throw new AfterSchoolScheduleValueInvalidException("스케줄은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        else if (!schedule.getType().equals(ScheduleType.AFTER_SCHOOL)) {
            throw new AfterSchoolScheduleValueInvalidException("스케줄 타입은 방과후여야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateAfterSchool(AfterSchoolEntity afterSchool) {
        if(afterSchool == null) {
            throw new AfterSchoolScheduleValueInvalidException("방과후는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
