package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.exception.SelfStudyScheduleValueInvalidException;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "self_study_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfStudyScheduleEntity extends BaseEntity {
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    private ScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "self_study_id")
    private SelfStudyEntity selfStudy;

    @Builder
    public SelfStudyScheduleEntity(ScheduleEntity schedule, PlaceEntity place, SelfStudyEntity selfStudy) {
        validateSchedule(schedule);
        validatePlace(place);
        validateSelfStudy(selfStudy);

        this.schedule = schedule;
        this.place = place;
        this.selfStudy = selfStudy;
    }


    private void validateSchedule(ScheduleEntity schedule) {
        if(schedule == null) {
            throw new SelfStudyScheduleValueInvalidException("스케줄은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        else if (!schedule.getType().equals(ScheduleType.SELF_STUDY)) {
            throw new SelfStudyScheduleValueInvalidException("스케줄 타입은 자습이여야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePlace(PlaceEntity place) {
        if(place == null) {
            throw new SelfStudyScheduleValueInvalidException("장소는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateSelfStudy(SelfStudyEntity selfStudy) {
        if(selfStudy == null) {
            throw new SelfStudyScheduleValueInvalidException("자습은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
