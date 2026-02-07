package solvit.teachmon.domain.student_schedule.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.exception.StudentScheduleValueInvalidException;
import solvit.teachmon.global.entity.BaseEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "student_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentScheduleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private SchoolPeriod period;

    @OneToMany(
            mappedBy = "studentSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ScheduleEntity> schedules = new ArrayList<>();

    @Builder
    public StudentScheduleEntity(StudentEntity student, LocalDate day, SchoolPeriod period) {
        validateStudent(student);
        validateDay(day);
        validatePeriod(period);

        this.student = student;
        this.day = day;
        this.period = period;
    }

    private void validateStudent(StudentEntity student) {
        if(student == null) {
            throw new StudentScheduleValueInvalidException("학생은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateDay(LocalDate day) {
        if(day == null) {
            throw new StudentScheduleValueInvalidException("날짜는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePeriod(SchoolPeriod period) {
        if(period == null) {
            throw new StudentScheduleValueInvalidException("교시는 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
