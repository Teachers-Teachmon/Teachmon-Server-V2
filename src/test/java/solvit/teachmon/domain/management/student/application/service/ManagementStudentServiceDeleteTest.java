package solvit.teachmon.domain.management.student.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 관리 서비스 - 학생 삭제 테스트")
class ManagementStudentServiceDeleteTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ManagementStudentService managementStudentService;

    @Test
    @DisplayName("학생을 삭제할 수 있다")
    void shouldDeleteStudent() {
        // Given: 학생 ID가 있을 때
        Long studentId = 1L;
        given(studentRepository.existsById(studentId)).willReturn(true);

        // When: 학생을 삭제하면
        managementStudentService.deleteStudent(studentId);

        // Then: 해당 ID의 학생이 삭제된다
        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    @DisplayName("여러 학생을 삭제할 수 있다")
    void shouldDeleteMultipleStudents() {
        // Given: 여러 학생 ID가 있을 때
        Long studentId1 = 1L;
        Long studentId2 = 2L;
        Long studentId3 = 3L;
        given(studentRepository.existsById(anyLong())).willReturn(true);

        // When: 학생들을 삭제하면
        managementStudentService.deleteStudent(studentId1);
        managementStudentService.deleteStudent(studentId2);
        managementStudentService.deleteStudent(studentId3);

        // Then: 각 ID의 학생이 삭제된다
        verify(studentRepository, times(1)).deleteById(studentId1);
        verify(studentRepository, times(1)).deleteById(studentId2);
        verify(studentRepository, times(1)).deleteById(studentId3);
    }

    @Test
    @DisplayName("존재하지 않는 학생 ID로 삭제하면 예외가 발생한다")
    void shouldThrowExceptionWhenStudentNotFoundOnDelete() {
        // Given: 존재하지 않는 학생 ID가 있을 때
        Long nonExistentId = 999L;
        given(studentRepository.existsById(nonExistentId)).willReturn(false);

        // When & Then: 삭제를 시도하면 예외가 발생한다
        assertThatThrownBy(() -> managementStudentService.deleteStudent(nonExistentId))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, times(1)).existsById(nonExistentId);
        verify(studentRepository, never()).deleteById(nonExistentId);
    }
}
