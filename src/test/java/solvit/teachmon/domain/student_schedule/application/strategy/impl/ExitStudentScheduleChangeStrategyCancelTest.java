package solvit.teachmon.domain.student_schedule.application.strategy.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.student_schedule.application.strategy.change.impl.ExitStudentScheduleChangeStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.ExitScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ExitRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.ExitScheduleRepository;
import solvit.teachmon.domain.student_schedule.exception.ExitScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.exception.ScheduleNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("이탈 학생 스케줄 변경 전략 - 취소 테스트")
class ExitStudentScheduleChangeStrategyCancelTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ExitRepository exitRepository;

    @Mock
    private ExitScheduleRepository exitScheduleRepository;

    @InjectMocks
    private ExitStudentScheduleChangeStrategy exitStudentScheduleChangeStrategy;

    @Test
    @DisplayName("이탈 처리를 취소할 수 있다")
    void shouldCancelExitSchedule() {
        // Given: 학생 스케줄, 스케줄, 이탈 스케줄, 이탈이 있을 때
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        ExitScheduleEntity exitSchedule = mock(ExitScheduleEntity.class);
        ExitEntity exit = mock(ExitEntity.class);

        given(studentSchedule.getId()).willReturn(1L);
        given(schedule.getId()).willReturn(10L);
        given(exitSchedule.getExit()).willReturn(exit);
        given(scheduleRepository.findByStudentScheduleIdAndType(1L, ScheduleType.EXIT))
                .willReturn(Optional.of(schedule));
        given(exitScheduleRepository.findByScheduleId(10L))
                .willReturn(Optional.of(exitSchedule));

        // When: 이탈을 취소하면
        exitStudentScheduleChangeStrategy.cancel(studentSchedule);

        // Then: 이탈이 삭제된다 (cascade로 스케줄도 삭제됨)
        verify(scheduleRepository, times(1)).findByStudentScheduleIdAndType(1L, ScheduleType.EXIT);
        verify(exitScheduleRepository, times(1)).findByScheduleId(10L);
        verify(exitRepository, times(1)).delete(exit);
    }

    @Test
    @DisplayName("스케줄이 존재하지 않으면 예외가 발생한다")
    void shouldThrowExceptionWhenScheduleNotFound() {
        // Given: 학생 스케줄은 있지만 이탈 스케줄이 없을 때
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        given(studentSchedule.getId()).willReturn(1L);
        given(scheduleRepository.findByStudentScheduleIdAndType(1L, ScheduleType.EXIT))
                .willReturn(Optional.empty());

        // When & Then: 취소를 시도하면 예외가 발생한다
        assertThatThrownBy(() -> exitStudentScheduleChangeStrategy.cancel(studentSchedule))
                .isInstanceOf(ScheduleNotFoundException.class);

        verify(scheduleRepository, times(1)).findByStudentScheduleIdAndType(1L, ScheduleType.EXIT);
        verify(exitScheduleRepository, never()).findByScheduleId(anyLong());
        verify(exitRepository, never()).delete(any());
        verify(scheduleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("이탈 스케줄이 존재하지 않으면 예외가 발생한다")
    void shouldThrowExceptionWhenExitScheduleNotFound() {
        // Given: 스케줄은 있지만 이탈 스케줄이 없을 때
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);

        given(studentSchedule.getId()).willReturn(1L);
        given(schedule.getId()).willReturn(10L);
        given(scheduleRepository.findByStudentScheduleIdAndType(1L, ScheduleType.EXIT))
                .willReturn(Optional.of(schedule));
        given(exitScheduleRepository.findByScheduleId(10L))
                .willReturn(Optional.empty());

        // When & Then: 취소를 시도하면 예외가 발생한다
        assertThatThrownBy(() -> exitStudentScheduleChangeStrategy.cancel(studentSchedule))
                .isInstanceOf(ExitScheduleNotFoundException.class);

        verify(scheduleRepository, times(1)).findByStudentScheduleIdAndType(1L, ScheduleType.EXIT);
        verify(exitScheduleRepository, times(1)).findByScheduleId(10L);
        verify(exitRepository, never()).delete(any());
        verify(scheduleRepository, never()).delete(any());
    }
}
