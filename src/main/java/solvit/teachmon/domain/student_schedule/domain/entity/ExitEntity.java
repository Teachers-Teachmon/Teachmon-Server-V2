package solvit.teachmon.domain.student_schedule.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.ExitScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.exception.ScheduleChangeAccessDeniedException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@Table(name = "`exit`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExitEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;

    @OneToMany(mappedBy = "exit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ExitScheduleEntity> exitSchedules;

    @Builder
    public ExitEntity(StudentEntity student, TeacherEntity teacher, LocalDate day, SchoolPeriod period) {
        this.student = student;
        this.teacher = teacher;
        this.day = day;
        this.period = period;
    }

    public static ExitEntity createExitEntity(StudentScheduleEntity studentSchedule, TeacherEntity teacher) {
        if(!teacher.hasStudentScheduleChangeAuthority()) {
            throw new ScheduleChangeAccessDeniedException();
        }

        return ExitEntity.builder()
                .student(studentSchedule.getStudent())
                .teacher(teacher)
                .day(studentSchedule.getDay())
                .period(studentSchedule.getPeriod())
                .build();
    }
}
