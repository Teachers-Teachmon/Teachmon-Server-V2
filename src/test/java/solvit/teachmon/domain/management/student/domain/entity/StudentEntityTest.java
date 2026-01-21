package solvit.teachmon.domain.management.student.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import solvit.teachmon.domain.management.student.exception.InvalidStudentInfoException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("학생 엔티티 테스트")
class StudentEntityTest {

    @Test
    @DisplayName("년도를 직접 지정하여 학생을 생성할 수 있다")
    void shouldCreateStudentWithSpecificYear() {
        // Given: 특정 년도와 학생 정보가 주어졌을 때
        Integer year = 2023;
        Integer grade = 2;
        Integer classNumber = 3;
        Integer number = 15;
        String name = "김철수";

        // When: 년도를 직접 지정하여 학생을 생성하면
        StudentEntity student = StudentEntity.withYearBuilder()
                .year(year)
                .grade(grade)
                .classNumber(classNumber)
                .number(number)
                .name(name)
                .build();

        // Then: 학생이 올바르게 생성된다
        assertThat(student.getYear()).isEqualTo(year);
        assertThat(student.getGrade()).isEqualTo(grade);
        assertThat(student.getClassNumber()).isEqualTo(classNumber);
        assertThat(student.getNumber()).isEqualTo(number);
        assertThat(student.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("현재 년도로 학생을 생성할 수 있다")
    void shouldCreateStudentWithCurrentYear() {
        // Given: 현재 년도와 학생 정보가 주어졌을 때
        Integer currentYear = LocalDateTime.now().getYear();
        Integer grade = 1;
        Integer classNumber = 5;
        Integer number = 20;
        String name = "이영희";

        // When: 현재 년도로 학생을 생성하면
        StudentEntity student = StudentEntity.withCurrentYearBuilder()
                .grade(grade)
                .classNumber(classNumber)
                .number(number)
                .name(name)
                .build();

        // Then: 현재 년도로 학생이 생성된다
        assertThat(student.getYear()).isEqualTo(currentYear);
        assertThat(student.getGrade()).isEqualTo(grade);
        assertThat(student.getClassNumber()).isEqualTo(classNumber);
        assertThat(student.getNumber()).isEqualTo(number);
        assertThat(student.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("년도가 null이면 InvalidStudentInfoException이 발생한다")
    void shouldThrowExceptionWhenYearIsNull() {
        // Given: 년도가 null인 상황에서
        Integer year = null;

        // When & Then: 학생을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> StudentEntity.withYearBuilder()
                .year(year)
                .grade(2)
                .classNumber(3)
                .number(15)
                .name("김철수")
                .build())
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("연도는 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("학년이 1~3 범위를 벗어나면 InvalidStudentInfoException이 발생한다")
    void shouldThrowExceptionWhenGradeIsOutOfRange() {
        // Given: 학년이 범위를 벗어난 상황에서
        Integer invalidGrade = 4;

        // When & Then: 학생을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> StudentEntity.withCurrentYearBuilder()
                .grade(invalidGrade)
                .classNumber(3)
                .number(15)
                .name("김철수")
                .build())
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("학년은 1~3학년 범위여야 합니다.");
    }

    @Test
    @DisplayName("학반이 null이면 InvalidStudentInfoException이 발생한다")
    void shouldThrowExceptionWhenClassNumberIsNull() {
        // Given: 학반이 null인 상황에서
        Integer classNumber = null;

        // When & Then: 학생을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> StudentEntity.withCurrentYearBuilder()
                .grade(2)
                .classNumber(classNumber)
                .number(15)
                .name("김철수")
                .build())
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("학반은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("학번이 1보다 작으면 InvalidStudentInfoException이 발생한다")
    void shouldThrowExceptionWhenNumberIsLessThanOne() {
        // Given: 학번이 1보다 작은 상황에서
        Integer invalidNumber = 0;

        // When & Then: 학생을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> StudentEntity.withCurrentYearBuilder()
                .grade(2)
                .classNumber(3)
                .number(invalidNumber)
                .name("김철수")
                .build())
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("학번은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("이름이 null이면 InvalidStudentInfoException이 발생한다")
    void shouldThrowExceptionWhenNameIsNull() {
        // Given: 이름이 null인 상황에서
        String name = null;

        // When & Then: 학생을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> StudentEntity.withCurrentYearBuilder()
                .grade(2)
                .classNumber(3)
                .number(15)
                .name(name)
                .build())
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("이름은 비어 있을 수 없습니다.");
    }
}