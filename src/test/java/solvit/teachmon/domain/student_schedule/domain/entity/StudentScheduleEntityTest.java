package solvit.teachmon.domain.student_schedule.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.exception.StudentScheduleValueInvalidException;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("학생 스케줄 엔티티 테스트")
class StudentScheduleEntityTest {

    @Test
    @DisplayName("유효한 값으로 학생 스케줄을 생성할 수 있다")
    void shouldCreateStudentScheduleWithValidValues() {
        // Given: 유효한 학생, 날짜, 교시가 있을 때
        StudentEntity student = createMockStudent(1L);
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = SchoolPeriod.SEVEN_PERIOD;

        // When & Then: 학생 스케줄을 생성하면 예외가 발생하지 않아야 한다
        // 🐛 BUG: 현재 코드는 NullPointerException이 발생합니다!
        // 왜냐하면 validation이 필드 할당 전에 실행되고,
        // this.student.equals(student)를 호출할 때 this.student가 null이기 때문입니다.
        StudentScheduleEntity studentSchedule = StudentScheduleEntity.builder()
                .student(student)
                .day(day)
                .period(period)
                .build();

        assertThat(studentSchedule.getStudent()).isEqualTo(student);
        assertThat(studentSchedule.getDay()).isEqualTo(day);
        assertThat(studentSchedule.getPeriod()).isEqualTo(period);
    }

    @Test
    @DisplayName("BUG: null 학생으로 학생 스케줄을 생성하면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenStudentIsNull() {
        // Given: 학생이 null일 때
        StudentEntity student = null;
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = SchoolPeriod.SEVEN_PERIOD;

        // When & Then: 학생 스케줄을 생성하면 예외가 발생해야 한다
        // 🐛 BUG: 현재 코드는 다음과 같은 문제가 있습니다:
        // 1. validateStudent(student)가 this.student.equals(student)를 호출
        // 2. this.student는 아직 할당되지 않아 null
        // 3. null.equals(student)는 NullPointerException 발생
        //
        // 올바른 동작:
        // - student가 null이면 StudentScheduleValueInvalidException 발생
        assertThatThrownBy(() -> StudentScheduleEntity.builder()
                .student(student)
                .day(day)
                .period(period)
                .build())
                .isInstanceOf(StudentScheduleValueInvalidException.class)
                .hasMessageContaining("학생은 null 일 수 없습니다");
    }

    @Test
    @DisplayName("BUG: null 날짜로 학생 스케줄을 생성하면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenDayIsNull() {
        // Given: 날짜가 null일 때
        StudentEntity student = createMockStudent(1L);
        LocalDate day = null;
        SchoolPeriod period = SchoolPeriod.SEVEN_PERIOD;

        // When & Then: 학생 스케줄을 생성하면 예외가 발생해야 한다
        // 🐛 BUG: 현재 코드는 다음과 같은 문제가 있습니다:
        // 1. validateDay(day)가 this.day.equals(day)를 호출
        // 2. this.day는 아직 할당되지 않아 null
        // 3. null.equals(day)는 NullPointerException 발생
        //
        // 올바른 동작:
        // - day가 null이면 StudentScheduleValueInvalidException 발생
        assertThatThrownBy(() -> StudentScheduleEntity.builder()
                .student(student)
                .day(day)
                .period(period)
                .build())
                .isInstanceOf(StudentScheduleValueInvalidException.class)
                .hasMessageContaining("날짜는 null 일 수 없습니다");
    }

    @Test
    @DisplayName("BUG: null 교시로 학생 스케줄을 생성하면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenPeriodIsNull() {
        // Given: 교시가 null일 때
        StudentEntity student = createMockStudent(1L);
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = null;

        // When & Then: 학생 스케줄을 생성하면 예외가 발생해야 한다
        // 🐛 BUG: 현재 코드는 다음과 같은 문제가 있습니다:
        // 1. validatePeriod(period)가 this.period.equals(period)를 호출
        // 2. this.period는 아직 할당되지 않아 null
        // 3. null.equals(period)는 NullPointerException 발생
        //
        // 올바른 동작:
        // - period가 null이면 StudentScheduleValueInvalidException 발생
        assertThatThrownBy(() -> StudentScheduleEntity.builder()
                .student(student)
                .day(day)
                .period(period)
                .build())
                .isInstanceOf(StudentScheduleValueInvalidException.class)
                .hasMessageContaining("교시는 null 일 수 없습니다");
    }

    @Test
    @DisplayName("BUG 분석: 현재 validation 로직의 문제점")
    void demonstrateValidationBug() {
        // 🐛 BUG 분석:
        //
        // 현재 StudentScheduleEntity의 생성자는 다음과 같이 동작합니다:
        //
        // @Builder
        // public StudentScheduleEntity(StudentEntity student, LocalDate day, SchoolPeriod period) {
        //     validateStudent(student);  // ❌ 이 시점에 this.student는 null
        //     validateDay(day);          // ❌ 이 시점에 this.day는 null
        //     validatePeriod(period);    // ❌ 이 시점에 this.period는 null
        //
        //     this.student = student;    // 할당은 validation 이후에 발생
        //     this.day = day;
        //     this.period = period;
        // }
        //
        // 그리고 validation 메서드들은:
        //
        // private void validateStudent(StudentEntity student) {
        //     if(!this.student.equals(student)) {  // ❌ this.student가 null이므로 NullPointerException
        //         throw new StudentScheduleValueInvalidException("학생은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        //     }
        // }
        //
        // 문제점:
        // 1. validation이 필드 할당 전에 실행됨
        // 2. null 객체에 대해 equals()를 호출하려고 시도
        // 3. 로직이 거꾸로 되어 있음 - 파라미터가 null인지 확인해야 하는데, 현재 필드와 다른지 확인함
        //
        // 올바른 validation:
        //
        // private void validateStudent(StudentEntity student) {
        //     if(student == null) {  // ✅ 파라미터가 null인지 확인
        //         throw new StudentScheduleValueInvalidException("학생은 null 일 수 없습니다.", HttpStatus.BAD_REQUEST);
        //     }
        // }
    }

    private StudentEntity createMockStudent(Long id) {
        StudentEntity student = mock(StudentEntity.class);
        given(student.getId()).willReturn(id);
        return student;
    }
}
