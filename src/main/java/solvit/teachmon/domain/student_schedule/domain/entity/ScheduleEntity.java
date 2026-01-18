package solvit.teachmon.domain.student_schedule.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_schedule_id")
    private StudentScheduleEntity studentSchedule;

    @Column(name = "stack_order", nullable = false)
    private Integer stackOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ScheduleType type;
}
