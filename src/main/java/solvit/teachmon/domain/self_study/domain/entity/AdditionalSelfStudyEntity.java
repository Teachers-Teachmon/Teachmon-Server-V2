package solvit.teachmon.domain.self_study.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AdditionalSelfStudyScheduleEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

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

    @OneToMany(mappedBy = "additionalSelfStudy", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AdditionalSelfStudyScheduleEntity> additionalSelfStudySchedules;

    @Builder
    public AdditionalSelfStudyEntity(LocalDate day, SchoolPeriod period, Integer grade) {
        this.day = day;
        this.period = period;
        this.grade = grade;
    }
}
