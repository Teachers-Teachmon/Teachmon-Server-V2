package solvit.teachmon.domain.management.teacher.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.global.enums.WeekDay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 금지날 조회 테스트")
class ManagementTeacherServiceGetTest {

    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;

    @InjectMocks
    private ManagementTeacherService managementTeacherService;

    @Test
    @DisplayName("선생님의 금지날 목록을 조회할 수 있다")
    void shouldGetTeacherBanDays() {
        // Given: 선생님 ID와 여러 개의 금지날이 있을 때
        Long teacherId = 1L;
        List<WeekDay> expectedBanDays = Arrays.asList(WeekDay.MON, WeekDay.WED, WeekDay.THU);

        given(supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId)).willReturn(expectedBanDays);

        // When: 금지날을 조회하면
        List<WeekDay> result = managementTeacherService.getTeacherBanDay(teacherId);

        // Then: 금지날 목록이 반환된다
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(WeekDay.MON, WeekDay.WED, WeekDay.THU);
        verify(supervisionBanDayRepository, times(1)).findWeekDaysByTeacherId(teacherId);
    }

    @Test
    @DisplayName("금지날이 없는 선생님의 경우 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoBanDays() {
        // Given: 금지날이 없는 선생님 ID가 있을 때
        Long teacherId = 1L;

        given(supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId)).willReturn(Collections.emptyList());

        // When: 금지날을 조회하면
        List<WeekDay> result = managementTeacherService.getTeacherBanDay(teacherId);

        // Then: 빈 리스트가 반환된다
        assertThat(result).isEmpty();
        verify(supervisionBanDayRepository, times(1)).findWeekDaysByTeacherId(teacherId);
    }

    @Test
    @DisplayName("하나의 금지날만 있을 때 조회할 수 있다")
    void shouldGetSingleBanDay() {
        // Given: 하나의 금지날만 있는 선생님 ID가 있을 때
        Long teacherId = 1L;
        List<WeekDay> expectedBanDays = Collections.singletonList(WeekDay.TUE);

        given(supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId)).willReturn(expectedBanDays);

        // When: 금지날을 조회하면
        List<WeekDay> result = managementTeacherService.getTeacherBanDay(teacherId);

        // Then: 하나의 금지날이 반환된다
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(WeekDay.TUE);
        verify(supervisionBanDayRepository, times(1)).findWeekDaysByTeacherId(teacherId);
    }

    @Test
    @DisplayName("모든 평일이 금지날인 경우 조회할 수 있다")
    void shouldGetAllWeekdaysAsBanDays() {
        // Given: 모든 평일이 금지날인 선생님 ID가 있을 때
        Long teacherId = 1L;
        List<WeekDay> expectedBanDays = Arrays.asList(
                WeekDay.MON, WeekDay.TUE, WeekDay.WED, WeekDay.THU
        );

        given(supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId)).willReturn(expectedBanDays);

        // When: 금지날을 조회하면
        List<WeekDay> result = managementTeacherService.getTeacherBanDay(teacherId);

        // Then: 모든 평일이 반환된다
        assertThat(result).hasSize(4);
        assertThat(result).containsExactlyInAnyOrder(
                WeekDay.MON, WeekDay.TUE, WeekDay.WED, WeekDay.THU
        );
        verify(supervisionBanDayRepository, times(1)).findWeekDaysByTeacherId(teacherId);
    }

    @Test
    @DisplayName("연속된 금지날을 조회할 수 있다")
    void shouldGetConsecutiveBanDays() {
        // Given: 연속된 금지날이 있는 선생님 ID가 있을 때
        Long teacherId = 1L;
        List<WeekDay> expectedBanDays = Arrays.asList(WeekDay.MON, WeekDay.TUE);

        given(supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId)).willReturn(expectedBanDays);

        // When: 금지날을 조회하면
        List<WeekDay> result = managementTeacherService.getTeacherBanDay(teacherId);

        // Then: 연속된 금지날이 반환된다
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(WeekDay.MON, WeekDay.TUE);
        verify(supervisionBanDayRepository, times(1)).findWeekDaysByTeacherId(teacherId);
    }
}
