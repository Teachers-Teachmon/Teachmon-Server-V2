package solvit.teachmon.domain.supervision.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import solvit.teachmon.domain.supervision.application.service.SupervisionAutoAssignService;
import solvit.teachmon.domain.supervision.application.service.SupervisionScheduleService;
import solvit.teachmon.domain.supervision.exception.InsufficientTeachersException;
import solvit.teachmon.domain.supervision.exception.InvalidDateRangeException;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("감독 자동 배정 컨트롤러 단순 테스트")
class SupervisionAutoAssignControllerSimpleTest {

    @InjectMocks
    private SupervisionScheduleController controller;

    @Mock
    private SupervisionAutoAssignService autoAssignService;

    @Mock
    private SupervisionScheduleService supervisionScheduleService;

    @Test
    @DisplayName("정상적인 기간으로 자동 배정 시 성공한다")
    void shouldAssignSupervisionSchedulesSuccessfully() {
        // Given: 정상적인 날짜 범위와 예상 응답
        LocalDate startDate = LocalDate.of(2025, 2, 3);
        LocalDate endDate = LocalDate.of(2025, 2, 6);
        
        List<SupervisionScheduleResponseDto> expectedResponse = createMockScheduleResponse();
        
        given(autoAssignService.autoAssignSupervisionSchedules(startDate, endDate))
                .willReturn(expectedResponse);

        // When: API 호출
        ResponseEntity<List<SupervisionScheduleResponseDto>> response = 
                controller.autoAssignSupervisionSchedules(startDate, endDate);

        // Then: 응답 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        // 서비스 호출 검증
        verify(autoAssignService).autoAssignSupervisionSchedules(startDate, endDate);
    }

    @Test
    @DisplayName("교사가 부족하면 예외가 발생한다")
    void shouldThrowExceptionWhenInsufficientTeachers() {
        // Given: 교사 부족 상황
        LocalDate startDate = LocalDate.of(2025, 2, 3);
        LocalDate endDate = LocalDate.of(2025, 2, 6);
        
        given(autoAssignService.autoAssignSupervisionSchedules(startDate, endDate))
                .willThrow(new InsufficientTeachersException("감독 배정 가능한 교사가 없습니다."));

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> controller.autoAssignSupervisionSchedules(startDate, endDate))
                .isInstanceOf(InsufficientTeachersException.class)
                .hasMessage("감독 배정 가능한 교사가 없습니다.");
    }

    @Test
    @DisplayName("잘못된 날짜 범위이면 예외가 발생한다")
    void shouldThrowExceptionWhenInvalidDateRange() {
        // Given: 잘못된 날짜 범위 (시작일이 종료일보다 늦음)
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate = LocalDate.of(2025, 2, 5);

        // When & Then: 예외 발생 확인 (Controller의 validation에서)
        assertThatThrownBy(() -> controller.autoAssignSupervisionSchedules(startDate, endDate))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    // Helper methods
    private List<SupervisionScheduleResponseDto> createMockScheduleResponse() {
        var teacherInfo = SupervisionScheduleResponseDto.SupervisionInfo.TeacherInfo.builder()
                .id(1L)
                .name("김선생")
                .build();

        var supervisionInfo = SupervisionScheduleResponseDto.SupervisionInfo.builder()
                .id(1L)
                .teacher(teacherInfo)
                .build();

        var schedule1 = SupervisionScheduleResponseDto.builder()
                .day(LocalDate.of(2025, 2, 3))
                .selfStudySupervision(supervisionInfo)
                .leaveSeatSupervision(supervisionInfo)
                .build();

        var schedule2 = SupervisionScheduleResponseDto.builder()
                .day(LocalDate.of(2025, 2, 4))
                .selfStudySupervision(supervisionInfo)
                .leaveSeatSupervision(supervisionInfo)
                .build();

        return List.of(schedule1, schedule2);
    }
}