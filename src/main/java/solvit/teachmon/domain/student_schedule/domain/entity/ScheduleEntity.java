package solvit.teachmon.domain.student_schedule.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.entity.BaseEntity;

import java.util.List;

@Getter
@Entity
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleEntity extends BaseEntity {
    public final static List<ScheduleType> ALLOWED_CHANGE_TYPES = List.of(
            ScheduleType.EXIT,
            ScheduleType.AWAY
    );

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_schedule_id")
    private StudentScheduleEntity studentSchedule;

    @Column(name = "stack_order", nullable = false)
    private Integer stackOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ScheduleType type;

    @Builder
    private ScheduleEntity(StudentScheduleEntity studentSchedule, Integer stackOrder, ScheduleType type) {
        this.studentSchedule = studentSchedule;
        this.stackOrder = stackOrder;
        this.type = type;
    }

    public static ScheduleEntity createNewStudentSchedule(StudentScheduleEntity studentSchedule, Integer nowStackOrder, ScheduleType type) {
        return ScheduleEntity.builder()
                .studentSchedule(studentSchedule)
                .stackOrder(nowStackOrder + 1)
                .type(type)
                .build();
    }
}