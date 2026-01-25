package solvit.teachmon.domain.leave_seat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import solvit.teachmon.domain.leave_seat.exception.LeaveSeatStudentInvalidException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "leave_seat_student")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveSeatStudentEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_seat_id")
    private LeaveSeatEntity leaveSeat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @Builder
    public LeaveSeatStudentEntity(LeaveSeatEntity leaveSeat, StudentEntity student) {
        this.leaveSeat = leaveSeat;
        this.student = student;
    }

    private void validateLeaveSeat(LeaveSeatEntity leaveSeat) {
        if (leaveSeat == null) {
            throw new LeaveSeatStudentInvalidException("leaveSeat(이석)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStudent(StudentEntity student) {
        if (student == null) {
            throw new LeaveSeatStudentInvalidException("student(학생)은 필수입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
