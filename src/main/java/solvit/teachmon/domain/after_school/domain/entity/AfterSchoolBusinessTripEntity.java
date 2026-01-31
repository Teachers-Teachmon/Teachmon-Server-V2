package solvit.teachmon.domain.after_school.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolBusinessTripException;
import solvit.teachmon.global.entity.BaseEntity;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "after_school_business_trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AfterSchoolBusinessTripEntity extends BaseEntity {
    @Column(name = "`day`", nullable = false)
    private LocalDate day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "after_school_id")
    private AfterSchoolEntity afterSchool;

    @Builder
    public AfterSchoolBusinessTripEntity(LocalDate day, AfterSchoolEntity afterSchool) {
        validateDay(day);
        validateAfterSchool(afterSchool);

        this.day = day;
        this.afterSchool = afterSchool;
    }

    private void validateDay(LocalDate day) {
        if (day == null) {
            throw new InvalidAfterSchoolBusinessTripException("출장 날짜는 필수입니다.");
        }
        if (day.isBefore(LocalDate.now())) {
            throw new InvalidAfterSchoolBusinessTripException("출장 날짜는 현재 날짜보다 이전일 수 없습니다.");
        }
    }

    private void validateAfterSchool(AfterSchoolEntity afterSchool) {
        if (afterSchool == null) {
            throw new InvalidAfterSchoolBusinessTripException("방과후는 필수입니다.");
        }
    }
}
