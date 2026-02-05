package solvit.teachmon.domain.leave_seat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.leave_seat.exception.FixedLeaveSeatStudentInvalidException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "fixed_leave_seat_student")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedLeaveSeatStudentEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fixed_leave_seat_id")
    private FixedLeaveSeatEntity fixedLeaveSeat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;
    
    @Builder
    public FixedLeaveSeatStudentEntity(FixedLeaveSeatEntity fixedLeaveSeat, StudentEntity student) {
        this.fixedLeaveSeat = fixedLeaveSeat;
        this.student = student;
    }

    private void validateLeaveSeat(FixedLeaveSeatEntity fixedLeaveSeat) {
        if (fixedLeaveSeat == null) {
            throw new FixedLeaveSeatStudentInvalidException("fixedLeaveSeat(고정 이석)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStudent(StudentEntity student) {
        if (student == null) {
            throw new FixedLeaveSeatStudentInvalidException("student(학생)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
