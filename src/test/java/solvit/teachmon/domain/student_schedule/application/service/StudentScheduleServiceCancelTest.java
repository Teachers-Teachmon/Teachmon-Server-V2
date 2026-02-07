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
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleCancelRequest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 서비스 - 학생 상태 변경 취소 테스트")
class StudentScheduleServiceCancelTest {

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @Mock
    private StudentScheduleChangeStrategyComposite studentScheduleChangeStrategyComposite;

    @InjectMocks
    private StudentScheduleService studentScheduleService;

    @Test
    @DisplayName("조퇴 처리를 취소할 수 있다")
    void shouldCancelAwaySchedule() {
        // Given: 학생 스케줄과 조퇴 취소 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        StudentScheduleCancelRequest request = new StudentScheduleCancelRequest(ScheduleType.AWAY);
        StudentScheduleChangeStrategy awayStrategy = mock(StudentScheduleChangeStrategy.class);

        given(studentScheduleRepository.findById(scheduleId)).willReturn(Optional.of(studentSchedule));
        given(studentScheduleChangeStrategyComposite.getStrategy(ScheduleType.AWAY)).willReturn(awayStrategy);

        // When: 조퇴 처리를 취소하면
        studentScheduleService.cancelStudentSchedule(scheduleId, request);

        // Then: Strategy의 cancel 메서드가 호출된다
        verify(studentScheduleRepository, times(1)).findById(scheduleId);
        verify(studentScheduleChangeStrategyComposite, times(1)).getStrategy(ScheduleType.AWAY);
        verify(awayStrategy, times(1)).cancel(studentSchedule);
    }

    @Test
    @DisplayName("이탈 처리를 취소할 수 있다")
    void shouldCancelExitSchedule() {
        // Given: 학생 스케줄과 이탈 취소 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        StudentScheduleCancelRequest request = new StudentScheduleCancelRequest(ScheduleType.EXIT);
        StudentScheduleChangeStrategy exitStrategy = mock(StudentScheduleChangeStrategy.class);

        given(studentScheduleRepository.findById(scheduleId)).willReturn(Optional.of(studentSchedule));
        given(studentScheduleChangeStrategyComposite.getStrategy(ScheduleType.EXIT)).willReturn(exitStrategy);

        // When: 이탈 처리를 취소하면
        studentScheduleService.cancelStudentSchedule(scheduleId, request);

        // Then: Strategy의 cancel 메서드가 호출된다
        verify(studentScheduleRepository, times(1)).findById(scheduleId);
        verify(studentScheduleChangeStrategyComposite, times(1)).getStrategy(ScheduleType.EXIT);
        verify(exitStrategy, times(1)).cancel(studentSchedule);
    }

    @Test
    @DisplayName("존재하지 않는 학생 스케줄 ID로 취소하면 예외가 발생한다")
    void shouldThrowExceptionWhenStudentScheduleNotFound() {
        // Given: 존재하지 않는 학생 스케줄 ID가 있을 때
        Long nonExistentId = 999L;
        StudentScheduleCancelRequest request = new StudentScheduleCancelRequest(ScheduleType.AWAY);

        given(studentScheduleRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then: 취소를 시도하면 예외가 발생한다
        assertThatThrownBy(() -> studentScheduleService.cancelStudentSchedule(nonExistentId, request))
                .isInstanceOf(StudentScheduleNotFoundException.class);

        verify(studentScheduleRepository, times(1)).findById(nonExistentId);
        verify(studentScheduleChangeStrategyComposite, never()).getStrategy(any());
    }
}
