package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
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
}
