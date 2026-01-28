package solvit.teachmon.domain.self_study.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.SelfStudyScheduleEntity;
import solvit.teachmon.domain.self_study.exception.InvalidSelfStudyInfoException;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Getter
@Entity
@Table(name = "self_study")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfStudyEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day", nullable = false)
    private WeekDay weekDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @OneToMany(mappedBy = "selfStudy", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SelfStudyScheduleEntity> selfStudySchedules;

    @Builder
    public SelfStudyEntity(BranchEntity branch, WeekDay weekDay, SchoolPeriod period, Integer grade) {
        validateBranch(branch);
        validateWeekDay(weekDay);
        validatePeriod(period);
        validateGrade(grade);

        this.branch = branch;
        this.weekDay = weekDay;
        this.period = period;
        this.grade = grade;
    }

    private void validateGrade(Integer grade) {
        if (grade == null || grade < 1 || grade > 3) {
            throw new InvalidSelfStudyInfoException("grade(학년)는 1 ~ 3 사이여야 합니다.");
        }
    }

    private void validatePeriod(SchoolPeriod period) {
        if (period == null) {
            throw new InvalidSelfStudyInfoException("period(교시)는 필수입니다.");
        }
    }

    private void validateWeekDay(WeekDay weekDay) {
        if (weekDay == null) {
            throw new InvalidSelfStudyInfoException("weekDay(요일)는 필수입니다.");
        }
    }

    private void validateBranch(BranchEntity branch) {
        if (branch == null) {
            throw new InvalidSelfStudyInfoException("branch(분기)는 필수입니다.");
        }
    }
}
