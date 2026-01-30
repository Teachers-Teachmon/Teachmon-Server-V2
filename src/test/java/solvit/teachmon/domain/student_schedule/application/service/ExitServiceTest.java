package solvit.teachmon.domain.student_schedule.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.student_schedule.application.mapper.ExitMapper;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.repository.ExitRepository;
import solvit.teachmon.domain.student_schedule.exception.ExitNotFoundException;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ExitHistoryResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("이탈 서비스 테스트")
class ExitServiceTest {

    @Mock
    private ExitRepository exitRepository;

    @Mock
    private ExitMapper exitMapper;

    @InjectMocks
    private ExitService exitService;

    @Test
    @DisplayName("이번주 이탈 학생 조회를 할 수 있다")
    void shouldGetWeekExitHistory() {
        // Given: 이번주에 이탈한 학생들이 있을 때
        ExitEntity exit1 = mock(ExitEntity.class);
        ExitEntity exit2 = mock(ExitEntity.class);

        List<ExitEntity> exits = List.of(exit1, exit2);

        ExitHistoryResponse response1 = ExitHistoryResponse.builder()
                .exitId(1L)
                .day(LocalDate.now())
                .teacher("홍선생님")
                .number(1101)
                .name("김학생")
                .period(SchoolPeriod.ONE_PERIOD)
                .build();

        ExitHistoryResponse response2 = ExitHistoryResponse.builder()
                .exitId(2L)
                .day(LocalDate.now().plusDays(1))
                .teacher("홍선생님")
                .number(2202)
                .name("이학생")
                .period(SchoolPeriod.TWO_PERIOD)
                .build();

        given(exitRepository.findAllByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(exits);
        given(exitMapper.toExitHistoryResponse(exit1)).willReturn(response1);
        given(exitMapper.toExitHistoryResponse(exit2)).willReturn(response2);

        // When: 이번주 이탈 조회를 하면
        List<ExitHistoryResponse> result = exitService.getWeekExitHistory();

        // Then: 이번주 이탈 학생 목록이 반환된다
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(response1, response2);

        verify(exitRepository, times(1))
                .findAllByDateRange(any(LocalDate.class), any(LocalDate.class));
        verify(exitMapper, times(2)).toExitHistoryResponse(any(ExitEntity.class));
    }

    @Test
    @DisplayName("날짜별 이탈 학생 조회를 할 수 있다")
    void shouldGetExitHistoryByDay() {
        // Given: 특정 날짜에 이탈한 학생들이 있을 때
        LocalDate targetDay = LocalDate.of(2024, 1, 15);

        ExitEntity exit = mock(ExitEntity.class);

        List<ExitEntity> exits = List.of(exit);

        ExitHistoryResponse response = ExitHistoryResponse.builder()
                .exitId(1L)
                .day(targetDay)
                .teacher("김선생님")
                .number(3105)
                .name("박학생")
                .period(SchoolPeriod.SEVEN_PERIOD)
                .build();

        given(exitRepository.findAllByDay(targetDay)).willReturn(exits);
        given(exitMapper.toExitHistoryResponse(exit)).willReturn(response);

        // When: 날짜별 이탈 조회를 하면
        List<ExitHistoryResponse> result = exitService.getExitHistoryByDay(targetDay);

        // Then: 해당 날짜의 이탈 학생 목록이 반환된다
        assertThat(result).hasSize(1);
        assertThat(result.get(0).exitId()).isEqualTo(1L);
        assertThat(result.get(0).day()).isEqualTo(targetDay);
        assertThat(result.get(0).teacher()).isEqualTo("김선생님");
        assertThat(result.get(0).number()).isEqualTo(3105);
        assertThat(result.get(0).name()).isEqualTo("박학생");

        verify(exitRepository, times(1)).findAllByDay(targetDay);
        verify(exitMapper, times(1)).toExitHistoryResponse(exit);
    }

    @Test
    @DisplayName("특정 날짜에 이탈 학생이 없으면 빈 리스트가 반환된다")
    void shouldReturnEmptyListWhenNoExitsOnDay() {
        // Given: 이탈 학생이 없는 날짜일 때
        LocalDate targetDay = LocalDate.of(2024, 1, 15);

        given(exitRepository.findAllByDay(targetDay)).willReturn(List.of());

        // When: 날짜별 이탈 조회를 하면
        List<ExitHistoryResponse> result = exitService.getExitHistoryByDay(targetDay);

        // Then: 빈 리스트가 반환된다
        assertThat(result).isEmpty();

        verify(exitRepository, times(1)).findAllByDay(targetDay);
        verify(exitMapper, never()).toExitHistoryResponse(any());
    }

    @Test
    @DisplayName("이탈 기록을 삭제할 수 있다")
    void shouldDeleteExit() {
        // Given: 이탈 기록이 존재할 때
        Long exitId = 1L;
        ExitEntity exit = mock(ExitEntity.class);

        given(exitRepository.findById(exitId)).willReturn(Optional.of(exit));
        doNothing().when(exitRepository).delete(exit);

        // When: 이탈 기록을 삭제하면
        exitService.deleteExit(exitId);

        // Then: 삭제가 수행된다
        verify(exitRepository, times(1)).findById(exitId);
        verify(exitRepository, times(1)).delete(exit);
    }

    @Test
    @DisplayName("존재하지 않는 이탈 기록을 삭제하려고 하면 예외가 발생한다")
    void shouldThrowExceptionWhenDeletingNonExistentExit() {
        // Given: 존재하지 않는 이탈 ID일 때
        Long exitId = 999L;

        given(exitRepository.findById(exitId)).willReturn(Optional.empty());

        // When & Then: 삭제 시도 시 예외가 발생한다
        assertThatThrownBy(() -> exitService.deleteExit(exitId))
                .isInstanceOf(ExitNotFoundException.class);

        verify(exitRepository, times(1)).findById(exitId);
        verify(exitRepository, never()).delete(any());
    }
}
