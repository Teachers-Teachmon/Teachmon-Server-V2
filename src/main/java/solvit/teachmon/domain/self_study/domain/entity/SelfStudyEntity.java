package solvit.teachmon.domain.self_study.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

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

    @Column(name = "year", nullable = false)
    private Integer year;

    @Builder
    public SelfStudyEntity(BranchEntity branch, WeekDay weekDay, SchoolPeriod period, Integer grade, Integer year) {
        this.branch = branch;
        this.weekDay = weekDay;
        this.period = period;
        this.grade = grade;
        this.year = year;
    }
}
