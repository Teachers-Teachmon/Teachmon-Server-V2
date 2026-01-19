package solvit.teachmon.domain.self_study.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.self_study.exception.InvalidAdditionalSelfStudyInfoException;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "additional_self_study")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdditionalSelfStudyEntity extends BaseEntity {
    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Builder
    public AdditionalSelfStudyEntity(LocalDate day, SchoolPeriod period, Integer grade) {
        validateDay(day);
        validatePeriod(period);
        validateGrade(grade);

        this.day = day;
        this.period = period;
        this.grade = grade;
    }

    private void validateGrade(Integer grade) {
        if (grade == null || grade < 1 || grade > 3) {
            throw new InvalidAdditionalSelfStudyInfoException("grade(학년)는 1 ~ 3 사이여야 합니다.");
        }
    }

    private void validateDay(LocalDate day) {
        if (day == null) {
            throw new InvalidAdditionalSelfStudyInfoException("day(날짜)는 필수입니다.");
        }
    }

    private void validatePeriod(SchoolPeriod period) {
        if (period == null) {
            throw new InvalidAdditionalSelfStudyInfoException("period(교시)는 필수입니다.");
        }
    }
}
