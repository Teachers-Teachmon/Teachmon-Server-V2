package solvit.teachmon.domain.student_schedule.application.strategy.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AwayScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.AwayRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AwayScheduleRepository;
import solvit.teachmon.domain.student_schedule.exception.AwayScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.exception.ScheduleNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("조퇴 학생 스케줄 변경 전략 - 취소 테스트")
class AwayStudentScheduleChangeStrategyCancelTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private AwayRepository awayRepository;

    @Mock
    private AwayScheduleRepository awayScheduleRepository;

    @InjectMocks
    private AwayStudentScheduleChangeStrategy awayStudentScheduleChangeStrategy;

    @Test
    @DisplayName("조퇴 처리를 취소할 수 있다")
    void shouldCancelAwaySchedule() {
        // Given: 학생 스케줄, 스케줄, 조퇴 스케줄, 조퇴가 있을 때
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        AwayScheduleEntity awaySchedule = mock(AwayScheduleEntity.class);
        AwayEntity away = mock(AwayEntity.class);

        given(studentSchedule.getId()).willReturn(1L);
        given(schedule.getId()).willReturn(10L);
        given(awaySchedule.getAway()).willReturn(away);
        given(scheduleRepository.findByStudentScheduleIdAndType(1L, ScheduleType.AWAY))
                .willReturn(Optional.of(schedule));
        given(awayScheduleRepository.findByScheduleId(10L))
                .willReturn(Optional.of(awaySchedule));

        // When: 조퇴를 취소하면
        awayStudentScheduleChangeStrategy.cancel(studentSchedule);

        // Then: 조퇴와 스케줄이 삭제된다
        verify(scheduleRepository, times(1)).findByStudentScheduleIdAndType(1L, ScheduleType.AWAY);
        verify(awayScheduleRepository, times(1)).findByScheduleId(10L);
        verify(awayRepository, times(1)).delete(away);
        verify(scheduleRepository, times(1)).delete(schedule);
    }

    @Test
    @DisplayName("스케줄이 존재하지 않으면 예외가 발생한다")
    void shouldThrowExceptionWhenScheduleNotFound() {
        // Given: 학생 스케줄은 있지만 조퇴 스케줄이 없을 때
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        given(studentSchedule.getId()).willReturn(1L);
        given(scheduleRepository.findByStudentScheduleIdAndType(1L, ScheduleType.AWAY))
                .willReturn(Optional.empty());

        // When & Then: 취소를 시도하면 예외가 발생한다
        assertThatThrownBy(() -> awayStudentScheduleChangeStrategy.cancel(studentSchedule))
                .isInstanceOf(ScheduleNotFoundException.class);

        verify(scheduleRepository, times(1)).findByStudentScheduleIdAndType(1L, ScheduleType.AWAY);
        verify(awayScheduleRepository, never()).findByScheduleId(anyLong());
        verify(awayRepository, never()).delete(any());
        verify(scheduleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("조퇴 스케줄이 존재하지 않으면 예외가 발생한다")
    void shouldThrowExceptionWhenAwayScheduleNotFound() {
        // Given: 스케줄은 있지만 조퇴 스케줄이 없을 때
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);

        given(studentSchedule.getId()).willReturn(1L);
        given(schedule.getId()).willReturn(10L);
        given(scheduleRepository.findByStudentScheduleIdAndType(1L, ScheduleType.AWAY))
                .willReturn(Optional.of(schedule));
        given(awayScheduleRepository.findByScheduleId(10L))
                .willReturn(Optional.empty());

        // When & Then: 취소를 시도하면 예외가 발생한다
        assertThatThrownBy(() -> awayStudentScheduleChangeStrategy.cancel(studentSchedule))
                .isInstanceOf(AwayScheduleNotFoundException.class);

        verify(scheduleRepository, times(1)).findByStudentScheduleIdAndType(1L, ScheduleType.AWAY);
        verify(awayScheduleRepository, times(1)).findByScheduleId(10L);
        verify(awayRepository, never()).delete(any());
        verify(scheduleRepository, never()).delete(any());
    }
}
