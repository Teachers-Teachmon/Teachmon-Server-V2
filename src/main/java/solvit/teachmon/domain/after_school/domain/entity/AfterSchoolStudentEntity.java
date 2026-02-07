package solvit.teachmon.domain.after_school.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolStudentException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "after_school_student")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AfterSchoolStudentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "after_school_id")
    private AfterSchoolEntity afterSchool;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @Builder
    public AfterSchoolStudentEntity(AfterSchoolEntity afterSchool, StudentEntity student) {
        validateAfterSchool(afterSchool);
        validateStudent(student);

        this.afterSchool = afterSchool;
        this.student = student;
    }

    private void validateAfterSchool(AfterSchoolEntity afterSchool) {
        if (afterSchool == null) {
            throw new InvalidAfterSchoolStudentException("방과후는 필수입니다.");
        }
    }

    private void validateStudent(StudentEntity student) {
        if (student == null) {
            throw new InvalidAfterSchoolStudentException("학생은 필수입니다.");
        }
    }
}
