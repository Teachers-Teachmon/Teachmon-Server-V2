package solvit.teachmon.domain.management.student.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.exception.InvalidStudentInfoException;
import solvit.teachmon.domain.management.student.domain.exception.StudentNotFoundException;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 관리 서비스 - 학생 정보 수정 테스트")
class ManagementStudentServiceUpdateTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ManagementStudentService managementStudentService;

    @Test
    @DisplayName("학생 정보를 수정할 수 있다")
    void shouldUpdateStudent() {
        // Given: 학생이 존재하고, 수정할 정보가 있을 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(1)
                .classNumber(1)
                .number(1)
                .name("김학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(2, 3, 15, "김수정");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When: 학생 정보를 수정하면
        managementStudentService.updateStudent(studentId, updateRequest);

        // Then: 학생 정보가 변경된다
        assertThat(student.getGrade()).isEqualTo(2);
        assertThat(student.getClassNumber()).isEqualTo(3);
        assertThat(student.getNumber()).isEqualTo(15);
        assertThat(student.getName()).isEqualTo("김수정");
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    @DisplayName("존재하지 않는 학생 ID로 수정하면 예외가 발생한다")
    void shouldThrowExceptionWhenStudentNotFoundOnUpdate() {
        // Given: 존재하지 않는 학생 ID가 있을 때
        Long studentId = 999L;
        StudentRequest updateRequest = new StudentRequest(2, 3, 15, "김학생");

        given(studentRepository.findById(studentId)).willReturn(Optional.empty());

        // When & Then: 수정하면 예외가 발생한다
        assertThatThrownBy(() -> managementStudentService.updateStudent(studentId, updateRequest))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    @DisplayName("학년만 변경할 수 있다")
    void shouldUpdateOnlyGrade() {
        // Given: 학생이 존재하고, 학년만 변경하려고 할 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(1)
                .classNumber(1)
                .number(5)
                .name("이학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(2, 1, 5, "이학생");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When: 학년을 수정하면
        managementStudentService.updateStudent(studentId, updateRequest);

        // Then: 학년만 변경되고 나머지는 유지된다
        assertThat(student.getGrade()).isEqualTo(2);
        assertThat(student.getClassNumber()).isEqualTo(1);
        assertThat(student.getNumber()).isEqualTo(5);
        assertThat(student.getName()).isEqualTo("이학생");
    }

    @Test
    @DisplayName("반과 번호만 변경할 수 있다")
    void shouldUpdateClassAndNumber() {
        // Given: 학생이 존재하고, 반과 번호만 변경하려고 할 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(2)
                .classNumber(1)
                .number(5)
                .name("박학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(2, 3, 10, "박학생");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When: 반과 번호를 수정하면
        managementStudentService.updateStudent(studentId, updateRequest);

        // Then: 반과 번호만 변경되고 학년과 이름은 유지된다
        assertThat(student.getGrade()).isEqualTo(2);
        assertThat(student.getClassNumber()).isEqualTo(3);
        assertThat(student.getNumber()).isEqualTo(10);
        assertThat(student.getName()).isEqualTo("박학생");
    }

    @Test
    @DisplayName("이름만 변경할 수 있다")
    void shouldUpdateOnlyName() {
        // Given: 학생이 존재하고, 이름만 변경하려고 할 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(3)
                .classNumber(2)
                .number(8)
                .name("최학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(3, 2, 8, "최수정");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When: 이름을 수정하면
        managementStudentService.updateStudent(studentId, updateRequest);

        // Then: 이름만 변경되고 나머지는 유지된다
        assertThat(student.getGrade()).isEqualTo(3);
        assertThat(student.getClassNumber()).isEqualTo(2);
        assertThat(student.getNumber()).isEqualTo(8);
        assertThat(student.getName()).isEqualTo("최수정");
    }

    @Test
    @DisplayName("잘못된 학년으로 수정하면 예외가 발생한다")
    void shouldThrowExceptionWhenInvalidGrade() {
        // Given: 학생이 존재하고, 잘못된 학년 정보가 있을 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(1)
                .classNumber(1)
                .number(1)
                .name("김학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(4, 1, 1, "김학생");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When & Then: 수정하면 예외가 발생한다
        assertThatThrownBy(() -> managementStudentService.updateStudent(studentId, updateRequest))
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("학년은 1 ~ 3 사이여야 합니다");
    }

    @Test
    @DisplayName("잘못된 반으로 수정하면 예외가 발생한다")
    void shouldThrowExceptionWhenInvalidClass() {
        // Given: 학생이 존재하고, 잘못된 반 정보가 있을 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(1)
                .classNumber(1)
                .number(1)
                .name("김학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(1, 0, 1, "김학생");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When & Then: 수정하면 예외가 발생한다
        assertThatThrownBy(() -> managementStudentService.updateStudent(studentId, updateRequest))
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("반은 1 이상이어야 합니다");
    }

    @Test
    @DisplayName("잘못된 번호로 수정하면 예외가 발생한다")
    void shouldThrowExceptionWhenInvalidNumber() {
        // Given: 학생이 존재하고, 잘못된 번호 정보가 있을 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(1)
                .classNumber(1)
                .number(1)
                .name("김학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(1, 1, 0, "김학생");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When & Then: 수정하면 예외가 발생한다
        assertThatThrownBy(() -> managementStudentService.updateStudent(studentId, updateRequest))
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("번호는 1 이상이어야 합니다");
    }

    @Test
    @DisplayName("빈 이름으로 수정하면 예외가 발생한다")
    void shouldThrowExceptionWhenEmptyName() {
        // Given: 학생이 존재하고, 빈 이름이 있을 때
        Long studentId = 1L;
        StudentEntity student = StudentEntity.builder()
                .year(LocalDate.now().getYear())
                .grade(1)
                .classNumber(1)
                .number(1)
                .name("김학생")
                .build();
        StudentRequest updateRequest = new StudentRequest(1, 1, 1, "   ");

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // When & Then: 수정하면 예외가 발생한다
        assertThatThrownBy(() -> managementStudentService.updateStudent(studentId, updateRequest))
                .isInstanceOf(InvalidStudentInfoException.class)
                .hasMessage("이름은 비어 있을 수 없습니다");
    }
}
