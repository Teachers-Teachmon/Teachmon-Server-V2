package solvit.teachmon.domain.management.student.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.management.student.exception.InvalidStudentInfoException;
import solvit.teachmon.global.entity.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 학생 정보를 관리하는 엔티티 클래스
 * Student information entity for managing student records
 * 
 * <p>학생은 학년도(year), 학년(grade, 1-3), 반(class), 번호(number), 이름(name)으로 구성됩니다.
 * Students are identified by year, grade (1-3 for middle school), class number, student number, and name.
 * 
 * <p>두 가지 빌더 패턴을 제공합니다:
 * Provides two builder patterns:
 * <ul>
 *   <li>withYearBuilder: 학년도를 직접 지정할 때 사용 (Used when specifying year explicitly)</li>
 *   <li>withCurrentYearBuilder: 현재 학년도로 자동 설정할 때 사용 (Used to automatically set current year)</li>
 * </ul>
 */
@Getter
@Entity
@Table(name = "student")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentEntity extends BaseEntity {
    /** 학년도 (Academic year) */
    @Column(name = "`year`", nullable = false)
    private Integer year;

    /** 학년 (1-3학년) (Grade level, must be 1-3 for middle school) */
    @Column(name = "grade", nullable = false)
    private Integer grade;

    /** 반 번호 (Class number) */
    @Column(name = "class", nullable = false)
    private Integer classNumber;

    /** 학생 번호 (Student number within the class) */
    @Column(name = "number", nullable = false)
    private Integer number;

    /** 학생 이름 (Student name) */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 학년도를 명시적으로 지정하여 학생 생성
     * Creates a student entity with explicitly specified academic year
     * 
     * @param year 학년도 (Academic year, must not be null)
     * @param grade 학년 (Grade level, must be 1-3)
     * @param classNumber 반 번호 (Class number, must not be null)
     * @param number 학생 번호 (Student number, must be >= 1)
     * @param name 학생 이름 (Student name, must not be null)
     */
    @Builder(builderMethodName = "withYearBuilder")
    public StudentEntity(Integer year, Integer grade, Integer classNumber, Integer number, String name) {
        validateYearField(year);
        validateGradeField(grade);
        validateClassNumberField(classNumber);
        validateNumberField(number);
        validateNameField(name);

        this.year = year;
        this.grade = grade;
        this.classNumber = classNumber;
        this.number = number;
        this.name = name;
    }

    /**
     * 현재 학년도로 자동 설정하여 학생 생성
     * Creates a student entity with current academic year automatically set
     * 
     * <p>학년도는 서버의 현재 시간 기준으로 자동 결정됩니다.
     * The academic year is automatically determined based on the server's current time.
     * 
     * @param grade 학년 (Grade level, must be 1-3)
     * @param classNumber 반 번호 (Class number, must not be null)
     * @param number 학생 번호 (Student number, must be >= 1)
     * @param name 학생 이름 (Student name, must not be null)
     */
    @Builder(builderMethodName = "withCurrentYearBuilder")
    public StudentEntity(Integer grade, Integer classNumber, Integer number, String name) {
        validateGradeField(grade);
        validateClassNumberField(classNumber);
        validateNumberField(number);
        validateNameField(name);

        this.year = getNowYear();
        this.grade = grade;
        this.classNumber = classNumber;
        this.number = number;
        this.name = name;
    }

    /**
     * 학생 정보 수정 (학년도 제외)
     * Updates student information (except academic year)
     * 
     * <p>학년도는 변경되지 않으며, 나머지 필드만 업데이트됩니다.
     * The academic year remains unchanged; only other fields are updated.
     * 
     * @param grade 새 학년 (New grade level, must be 1-3)
     * @param classNumber 새 반 번호 (New class number, must not be null)
     * @param number 새 학생 번호 (New student number, must be >= 1)
     * @param name 새 학생 이름 (New student name, must not be null)
     */
    public void changeInfo(Integer grade, Integer classNumber, Integer number, String name) {
        validateGradeField(grade);
        validateClassNumberField(classNumber);
        validateNumberField(number);
        validateNameField(name);

        this.grade = grade;
        this.classNumber = classNumber;
        this.number = number;
        this.name = name;
    }
  
    /**
     * 현재 학년도를 반환
     * Returns the current academic year based on server time
     * 
     * @return 현재 연도 (Current year)
     */
    private Integer getNowYear() {
        return LocalDateTime.now().getYear();
    }

    /**
     * 학년도 필드 유효성 검증
     * Validates the year field
     * 
     * @param year 학년도 (Academic year to validate)
     * @throws InvalidStudentInfoException 학년도가 null인 경우 (if year is null)
     */
    private void validateYearField(Integer year) {
        if(year == null)
            throw new InvalidStudentInfoException("연도는 비어 있을 수 없습니다.");
    }

    /**
     * 학년 필드 유효성 검증 (1-3학년 범위)
     * Validates the grade field (must be 1-3 for middle school)
     * 
     * @param grade 학년 (Grade to validate)
     * @throws InvalidStudentInfoException 학년이 null이거나 1-3 범위를 벗어난 경우 (if grade is null or out of 1-3 range)
     */
    private void validateGradeField(Integer grade) {
        if(grade == null || 1 > grade || 3 < grade)
            throw new InvalidStudentInfoException("학년은 1~3학년 범위여야 합니다.");
    }

    /**
     * 반 번호 필드 유효성 검증
     * Validates the class number field
     * 
     * @param classNumber 반 번호 (Class number to validate)
     * @throws InvalidStudentInfoException 반 번호가 null인 경우 (if classNumber is null)
     */
    private void validateClassNumberField(Integer classNumber) {
        if(classNumber == null)
            throw new InvalidStudentInfoException("학반은 비어 있을 수 없습니다.");
    }

    /**
     * 학생 번호 필드 유효성 검증 (1 이상)
     * Validates the student number field (must be >= 1)
     * 
     * @param number 학생 번호 (Student number to validate)
     * @throws InvalidStudentInfoException 학생 번호가 null이거나 1 미만인 경우 (if number is null or less than 1)
     */
    private void validateNumberField(Integer number) {
        if(number == null || number < 1)
            throw new InvalidStudentInfoException("학번은 1 이상이어야 합니다.");
    }

    /**
     * 이름 필드 유효성 검증
     * Validates the name field
     * 
     * @param name 학생 이름 (Student name to validate)
     * @throws InvalidStudentInfoException 이름이 null인 경우 (if name is null)
     */
    private void validateNameField(String name) {
        if(name == null)
            throw new InvalidStudentInfoException("이름은 비어 있을 수 없습니다.");
    }
}