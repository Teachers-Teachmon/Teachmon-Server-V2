package solvit.teachmon.domain.student_schedule.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.student_schedule.application.strategy.change.StudentScheduleChangeStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.change.StudentScheduleChangeStrategyComposite;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.exception.StudentScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleUpdateRequest;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 서비스 - 학생 상태 변경 테스트")
class StudentScheduleServiceUpdateTest {

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @Mock
    private StudentScheduleChangeStrategyComposite studentScheduleChangeStrategyComposite;

    @InjectMocks
    private StudentScheduleService studentScheduleService;

    @Test
    @DisplayName("학생 상태를 조퇴로 변경할 수 있다")
    void shouldUpdateStudentScheduleToAway() {
        // Given: 학생 스케줄과 조퇴 변경 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        TeacherEntity teacher = mock(TeacherEntity.class);
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.AWAY);
        StudentScheduleChangeStrategy awayStrategy = mock(StudentScheduleChangeStrategy.class);

        given(studentScheduleRepository.findById(scheduleId)).willReturn(Optional.of(studentSchedule));
        given(studentScheduleChangeStrategyComposite.getStrategy(ScheduleType.AWAY)).willReturn(awayStrategy);

        // When: 학생 상태를 조퇴로 변경하면
        studentScheduleService.updateStudentSchedule(scheduleId, request, teacher);

        // Then: Strategy의 change 메서드가 호출된다
        verify(studentScheduleRepository, times(1)).findById(scheduleId);
        verify(studentScheduleChangeStrategyComposite, times(1)).getStrategy(ScheduleType.AWAY);
        verify(awayStrategy, times(1)).change(studentSchedule, teacher);
    }

    @Test
    @DisplayName("학생 상태를 이탈로 변경할 수 있다")
    void shouldUpdateStudentScheduleToExit() {
        // Given: 학생 스케줄과 이탈 변경 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        TeacherEntity teacher = mock(TeacherEntity.class);
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.EXIT);
        StudentScheduleChangeStrategy exitStrategy = mock(StudentScheduleChangeStrategy.class);

        given(studentScheduleRepository.findById(scheduleId)).willReturn(Optional.of(studentSchedule));
        given(studentScheduleChangeStrategyComposite.getStrategy(ScheduleType.EXIT)).willReturn(exitStrategy);

        // When: 학생 상태를 이탈로 변경하면
        studentScheduleService.updateStudentSchedule(scheduleId, request, teacher);

        // Then: Strategy의 change 메서드가 호출된다
        verify(studentScheduleRepository, times(1)).findById(scheduleId);
        verify(studentScheduleChangeStrategyComposite, times(1)).getStrategy(ScheduleType.EXIT);
        verify(exitStrategy, times(1)).change(studentSchedule, teacher);
    }

    @Test
    @DisplayName("존재하지 않는 학생 스케줄 ID로 변경하면 예외가 발생한다")
    void shouldThrowExceptionWhenStudentScheduleNotFound() {
        // Given: 존재하지 않는 학생 스케줄 ID가 있을 때
        Long nonExistentId = 999L;
        TeacherEntity teacher = mock(TeacherEntity.class);
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.AWAY);

        given(studentScheduleRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then: 변경을 시도하면 예외가 발생한다
        assertThatThrownBy(() -> studentScheduleService.updateStudentSchedule(nonExistentId, request, teacher))
                .isInstanceOf(StudentScheduleNotFoundException.class);

        verify(studentScheduleRepository, times(1)).findById(nonExistentId);
        verify(studentScheduleChangeStrategyComposite, never()).getStrategy(any());
    }

    @Test
    @DisplayName("학생 상태를 자습으로 변경할 수 있다")
    void shouldUpdateStudentScheduleToSelfStudy() {
        // Given: 학생 스케줄과 자습 변경 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        TeacherEntity teacher = mock(TeacherEntity.class);
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.SELF_STUDY);
        StudentScheduleChangeStrategy selfStudyStrategy = mock(StudentScheduleChangeStrategy.class);

        given(studentScheduleRepository.findById(scheduleId)).willReturn(Optional.of(studentSchedule));
        given(studentScheduleChangeStrategyComposite.getStrategy(ScheduleType.SELF_STUDY)).willReturn(selfStudyStrategy);

        // When: 학생 상태를 자습으로 변경하면
        studentScheduleService.updateStudentSchedule(scheduleId, request, teacher);

        // Then: Strategy의 change 메서드가 호출된다
        verify(studentScheduleRepository, times(1)).findById(scheduleId);
        verify(studentScheduleChangeStrategyComposite, times(1)).getStrategy(ScheduleType.SELF_STUDY);
        verify(selfStudyStrategy, times(1)).change(studentSchedule, teacher);
    }

    @Test
    @DisplayName("학생 상태를 방과후로 변경할 수 있다")
    void shouldUpdateStudentScheduleToAfterSchool() {
        // Given: 학생 스케줄과 방과후 변경 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        TeacherEntity teacher = mock(TeacherEntity.class);
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.AFTER_SCHOOL);
        StudentScheduleChangeStrategy afterSchoolStrategy = mock(StudentScheduleChangeStrategy.class);

        given(studentScheduleRepository.findById(scheduleId)).willReturn(Optional.of(studentSchedule));
        given(studentScheduleChangeStrategyComposite.getStrategy(ScheduleType.AFTER_SCHOOL)).willReturn(afterSchoolStrategy);

        // When: 학생 상태를 방과후로 변경하면
        studentScheduleService.updateStudentSchedule(scheduleId, request, teacher);

        // Then: Strategy의 change 메서드가 호출된다
        verify(studentScheduleRepository, times(1)).findById(scheduleId);
        verify(studentScheduleChangeStrategyComposite, times(1)).getStrategy(ScheduleType.AFTER_SCHOOL);
        verify(afterSchoolStrategy, times(1)).change(studentSchedule, teacher);
    }
}
