package solvit.teachmon.domain.management.teacher.application.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 선생님 삭제 테스트")
class ManagementTeacherFacadeServiceDeleteTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SupervisionScheduleRepository supervisionScheduleRepository;

    @InjectMocks
    private ManagementTeacherFacadeService managementTeacherFacadeService;

    @Test
    @DisplayName("선생님을 삭제하면 repository.deleteById가 호출된다")
    void shouldDeleteTeacher() {
        // Given
        Long teacherId = 1L;
        given(teacherRepository.existsById(teacherId)).willReturn(true);

        // When
        managementTeacherFacadeService.deleteTeacher(teacherId);

        // Then
        verify(teacherRepository, times(1)).existsById(teacherId);
        verify(teacherRepository, times(1)).deleteById(teacherId);
    }

    @Test
    @DisplayName("존재하지 않는 선생님을 삭제하면 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotFoundOnDelete() {
        // Given
        Long teacherId = 999L;
        given(teacherRepository.existsById(teacherId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> managementTeacherFacadeService.deleteTeacher(teacherId))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(teacherRepository, times(1)).existsById(teacherId);
        verify(teacherRepository, never()).deleteById(teacherId);
    }
}
