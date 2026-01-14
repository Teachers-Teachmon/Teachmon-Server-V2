package solvit.teachmon.domain.management.student.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 관리 서비스 - 학생 생성 테스트")
class ManagementStudentServiceCreateTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ManagementStudentService managementStudentService;

    @Captor
    private ArgumentCaptor<StudentEntity> studentCaptor;

    @Test
    @DisplayName("학생을 생성할 수 있다")
    void shouldCreateStudent() {
        // Given: 학생 정보가 있을 때
        StudentRequest request = new StudentRequest(1, 1, 1, "김학생");

        // When: 학생을 생성하면
        managementStudentService.createStudent(request);

        // Then: 학생이 저장된다
        verify(studentRepository, times(1)).save(studentCaptor.capture());

        StudentEntity savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getYear()).isEqualTo(LocalDate.now().getYear());
        assertThat(savedStudent.getGrade()).isEqualTo(1);
        assertThat(savedStudent.getClassNumber()).isEqualTo(1);
        assertThat(savedStudent.getNumber()).isEqualTo(1);
        assertThat(savedStudent.getName()).isEqualTo("김학생");
    }

    @Test
    @DisplayName("2학년 학생을 생성할 수 있다")
    void shouldCreateSecondGradeStudent() {
        // Given: 2학년 학생 정보가 있을 때
        StudentRequest request = new StudentRequest(2, 3, 15, "이학생");

        // When: 학생을 생성하면
        managementStudentService.createStudent(request);

        // Then: 2학년 학생이 저장된다
        verify(studentRepository, times(1)).save(studentCaptor.capture());

        StudentEntity savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getYear()).isEqualTo(LocalDate.now().getYear());
        assertThat(savedStudent.getGrade()).isEqualTo(2);
        assertThat(savedStudent.getClassNumber()).isEqualTo(3);
        assertThat(savedStudent.getNumber()).isEqualTo(15);
        assertThat(savedStudent.getName()).isEqualTo("이학생");
    }

    @Test
    @DisplayName("3학년 학생을 생성할 수 있다")
    void shouldCreateThirdGradeStudent() {
        // Given: 3학년 학생 정보가 있을 때
        StudentRequest request = new StudentRequest(3, 5, 20, "박학생");

        // When: 학생을 생성하면
        managementStudentService.createStudent(request);

        // Then: 3학년 학생이 저장된다
        verify(studentRepository, times(1)).save(studentCaptor.capture());

        StudentEntity savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getYear()).isEqualTo(LocalDate.now().getYear());
        assertThat(savedStudent.getGrade()).isEqualTo(3);
        assertThat(savedStudent.getClassNumber()).isEqualTo(5);
        assertThat(savedStudent.getNumber()).isEqualTo(20);
        assertThat(savedStudent.getName()).isEqualTo("박학생");
    }

    @Test
    @DisplayName("현재 년도로 학생을 생성한다")
    void shouldCreateStudentWithCurrentYear() {
        // Given: 학생 정보가 있을 때
        StudentRequest request = new StudentRequest(1, 1, 1, "최학생");
        int currentYear = LocalDate.now().getYear();

        // When: 학생을 생성하면
        managementStudentService.createStudent(request);

        // Then: 현재 년도로 학생이 저장된다
        verify(studentRepository, times(1)).save(studentCaptor.capture());

        StudentEntity savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getYear()).isEqualTo(currentYear);
    }

    @Test
    @DisplayName("다양한 반과 번호로 학생을 생성할 수 있다")
    void shouldCreateStudentWithVariousClassAndNumber() {
        // Given: 다양한 반과 번호의 학생 정보가 있을 때
        StudentRequest request = new StudentRequest(2, 8, 25, "정학생");

        // When: 학생을 생성하면
        managementStudentService.createStudent(request);

        // Then: 해당 정보로 학생이 저장된다
        verify(studentRepository, times(1)).save(studentCaptor.capture());

        StudentEntity savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getGrade()).isEqualTo(2);
        assertThat(savedStudent.getClassNumber()).isEqualTo(8);
        assertThat(savedStudent.getNumber()).isEqualTo(25);
        assertThat(savedStudent.getName()).isEqualTo("정학생");
    }
}
