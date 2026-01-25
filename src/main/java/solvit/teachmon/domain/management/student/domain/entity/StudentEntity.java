package solvit.teachmon.domain.management.student.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import solvit.teachmon.domain.management.student.exception.InvalidStudentInfoException;
import solvit.teachmon.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "student")
public class StudentEntity extends BaseEntity {
    @Column(name = "`year`", nullable = false)
    private Integer year;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "class", nullable = false)
    private Integer classNumber;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder(builderMethodName = "withYearBuilder")
    public StudentEntity(Integer year, Integer grade, Integer classNumber, Integer number, String name) {
        validateYear(year);
        validateGrade(grade);
        validateClassNumber(classNumber);
        validateNumber(number);
        validateName(name);

        this.year = year;
        this.grade = grade;
        this.classNumber = classNumber;
        this.number = number;
        this.name = name;
    }

    @Builder(builderMethodName = "withCurrentYearBuilder")
    public StudentEntity(Integer grade, Integer classNumber, Integer number, String name) {
        validateGrade(grade);
        validateClassNumber(classNumber);
        validateNumber(number);
        validateName(name);

        this.year = getNowYear();
        this.grade = grade;
        this.classNumber = classNumber;
        this.number = number;
        this.name = name;
    }

    private Integer getNowYear() {
        return LocalDateTime.now().getYear();
    }

    private void validateYear(Integer year) {
        if(year == null)
            throw new InvalidStudentInfoException("연도는 비어 있을 수 없습니다.");
    }

    private void validateGrade(Integer grade) {
        if(grade == null || 1 > grade || 3 < grade)
            throw new InvalidStudentInfoException("학년은 1~3학년 범위여야 합니다.");
    }

    private void validateClassNumber(Integer classNumber) {
        if(classNumber == null)
            throw new InvalidStudentInfoException("학반은 비어 있을 수 없습니다.");
    }

    private void validateNumber(Integer number) {
        if(number == null || number < 1)
            throw new InvalidStudentInfoException("학번은 1 이상이어야 합니다.");
    }

    private void validateName(String name) {
        if(name == null)
            throw new InvalidStudentInfoException("이름은 비어 있을 수 없습니다.");
    }
}
