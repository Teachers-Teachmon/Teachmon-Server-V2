package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.exception.AdditionalSelfStudyScheduleValueInvalidException;
import solvit.teachmon.domain.student_schedule.exception.SelfStudyScheduleValueInvalidException;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "additional_self_study_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdditionalSelfStudyScheduleEntity extends BaseEntity {
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    private ScheduleEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "additional_self_study_id")
    private AdditionalSelfStudyEntity additionalSelfStudy;

    @Builder
    public AdditionalSelfStudyScheduleEntity(
            ScheduleEntity schedule,
            PlaceEntity place,
            AdditionalSelfStudyEntity additionalSelfStudy
    ) {
        validateSchedule(schedule);
        validatePlace(place);
        validateAdditionalSelfStudy(additionalSelfStudy);

        this.schedule = schedule;
        this.place = place;
        this.additionalSelfStudy = additionalSelfStudy;
    }

    private void validateSchedule(ScheduleEntity schedule) {
        if(schedule == null) {
            throw new SelfStudyScheduleValueInvalidException("스케줄은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        else if(!schedule.getType().equals(ScheduleType.ADDITIONAL_SELF_STUDY)) {
            throw new AdditionalSelfStudyScheduleValueInvalidException("스케줄 타입은 추가 자습이여야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePlace(PlaceEntity place) {
        if(place == null) {
            throw new AdditionalSelfStudyScheduleValueInvalidException("장소는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateAdditionalSelfStudy(AdditionalSelfStudyEntity additionalSelfStudy) {
        if(additionalSelfStudy == null) {
            throw new AdditionalSelfStudyScheduleValueInvalidException("추가 자습은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
