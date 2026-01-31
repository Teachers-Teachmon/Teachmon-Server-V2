package solvit.teachmon.domain.after_school.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolReinforcementException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "after_school_reinforcement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AfterSchoolReinforcementEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "after_school_id")
    private AfterSchoolEntity afterSchool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_place_id")
    private PlaceEntity place;

    @Column(name = "change_day", nullable = false)
    private LocalDate changeDay;

    @Column(name = "change_period", nullable = false)
    private SchoolPeriod changePeriod;

    @Builder
    public AfterSchoolReinforcementEntity(AfterSchoolEntity afterSchool, PlaceEntity place, LocalDate changeDay, SchoolPeriod changePeriod) {
        validateAfterSchool(afterSchool);
        validatePlace(place);
        validateChangePeriod(changePeriod);
        validateChangeDay(changeDay);

        this.afterSchool = afterSchool;
        this.place = place;
        this.changePeriod = changePeriod;
        this.changeDay = changeDay;
    }

    private void validateAfterSchool(AfterSchoolEntity afterSchool) {
        if (afterSchool == null) {
            throw new InvalidAfterSchoolReinforcementException("방과후는 필수입니다.");
        }
    }

    private void validatePlace(PlaceEntity place) {
        if (place == null) {
            throw new InvalidAfterSchoolReinforcementException("보강 장소는 필수입니다.");
        }
    }

    private void validateChangePeriod(SchoolPeriod changePeriod) {
        if (changePeriod == null) {
            throw new InvalidAfterSchoolReinforcementException("보강 교시는 필수입니다.");
        }
    }

    private void validateChangeDay(LocalDate changeDay) {
        if (changeDay == null) {
            throw new InvalidAfterSchoolReinforcementException("보강 날짜는 필수입니다.");
        }
        if (changeDay.isBefore(LocalDate.now())) {
            throw new InvalidAfterSchoolReinforcementException("보강 날짜는 현재 날짜보다 이전일 수 없습니다.");
        }
    }
}
