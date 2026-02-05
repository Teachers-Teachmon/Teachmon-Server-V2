package solvit.teachmon.domain.supervision.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.supervision.domain.vo.TeacherSupervisionInfo;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionAutoAssignRepository;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.supervision.domain.strategy.SupervisionPriorityStrategy;
import solvit.teachmon.domain.supervision.exception.InsufficientTeachersException;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;
import solvit.teachmon.domain.supervision.application.mapper.SupervisionResponseMapper;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("감독 자동 배정 서비스 테스트")
class SupervisionAutoAssignServiceTest {

    @Mock
    private SupervisionScheduleRepository scheduleRepository;
    
    @Mock
    private TeacherSupervisionInfoService teacherSupervisionInfoService;
    
    @Mock
    private SupervisionAssignmentProcessor assignmentProcessor;
    
    @Mock
    private SupervisionDateExtractor dateExtractor;
    
    @Mock
    private SupervisionResponseMapper responseMapper;

    private SupervisionAutoAssignService autoAssignService;
    
    // 테스트 데이터
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        autoAssignService = new SupervisionAutoAssignService(
                scheduleRepository, teacherSupervisionInfoService, assignmentProcessor, dateExtractor, responseMapper);
        
        // 테스트 기간 설정 (2025-02-03 ~ 2025-02-06, 월~목)
        startDate = LocalDate.of(2025, 2, 3);
        endDate = LocalDate.of(2025, 2, 6);
    }

    @Test
    @DisplayName("정상적인 기간으로 자동 배정 시 성공적으로 스케줄이 생성된다")
    void shouldCreateSchedulesSuccessfullyWhenValidPeriodProvided() {
        // Given: 모든 서비스들이 성공적인 응답을 반환하도록 설정
        List<TeacherSupervisionInfo> teacherInfos = List.of(
                TeacherSupervisionInfo.builder()
                        .teacherId(1L)
                        .teacherName("김선생")
                        .banDays(Set.of())
                        .totalSupervisionCount(0)
                        .supervisionCounts(new HashMap<>())
                        .build(),
                TeacherSupervisionInfo.builder()
                        .teacherId(2L)
                        .teacherName("이선생")
                        .banDays(Set.of())
                        .totalSupervisionCount(0)
                        .supervisionCounts(new HashMap<>())
                        .build()
        );
        
        List<LocalDate> targetDates = List.of(
                startDate, startDate.plusDays(1), startDate.plusDays(2), startDate.plusDays(3)
        );
        
        List<solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity> schedules = new ArrayList<>();
        
        List<SupervisionScheduleResponseDto> expectedResult = List.of(
                SupervisionScheduleResponseDto.builder()
                        .day(startDate)
                        .build()
        );
        
        given(teacherSupervisionInfoService.getTeacherSupervisionInfos()).willReturn(teacherInfos);
        given(dateExtractor.extractWeekdays(startDate, endDate)).willReturn(targetDates);
        given(assignmentProcessor.processDateAssignments(targetDates, teacherInfos)).willReturn(schedules);
        given(scheduleRepository.saveAll(schedules)).willReturn(schedules);
        given(responseMapper.convertToResponseDtos(schedules)).willReturn(expectedResult);

        // When: 자동 배정 실행
        List<SupervisionScheduleResponseDto> result = autoAssignService.autoAssignSupervisionSchedules(startDate, endDate);

        // Then: 결과가 반환됨
        assertThat(result).isEqualTo(expectedResult);
        
        // 모든 서비스 호출 검증
        verify(teacherSupervisionInfoService).getTeacherSupervisionInfos();
        verify(dateExtractor).extractWeekdays(startDate, endDate);
        verify(assignmentProcessor).processDateAssignments(targetDates, teacherInfos);
        verify(scheduleRepository).saveAll(schedules);
        verify(responseMapper).convertToResponseDtos(schedules);
    }

    @Test
    @DisplayName("교사 정보가 없을 때 예외가 전파된다")
    void shouldPropagateExceptionWhenNoTeachersAvailable() {
        // Given: teacherSupervisionInfoService에서 예외 발생
        given(teacherSupervisionInfoService.getTeacherSupervisionInfos())
                .willThrow(new InsufficientTeachersException("감독 배정 가능한 교사가 없습니다."));

        // When & Then: 예외가 전파됨
        assertThatThrownBy(() -> autoAssignService.autoAssignSupervisionSchedules(startDate, endDate))
                .isInstanceOf(InsufficientTeachersException.class)
                .hasMessage("감독 배정 가능한 교사가 없습니다.");
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦으면 예외가 발생한다")
    void shouldThrowExceptionWhenStartDateAfterEndDate() {
        // Given: 잘못된 날짜 범위
        LocalDate invalidStartDate = LocalDate.of(2025, 2, 10);
        LocalDate invalidEndDate = LocalDate.of(2025, 2, 5);

        // When & Then: 예외 발생
        assertThatThrownBy(() -> autoAssignService.autoAssignSupervisionSchedules(invalidStartDate, invalidEndDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작일")
                .hasMessageContaining("종료일")
                .hasMessageContaining("늦을 수 없습니다");
    }

    @Test
    @DisplayName("null 날짜가 전달되면 예외가 발생한다")
    void shouldThrowExceptionWhenDateIsNull() {
        // When & Then: null 시작일
        assertThatThrownBy(() -> autoAssignService.autoAssignSupervisionSchedules(null, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작일과 종료일은 필수입니다.");

        // When & Then: null 종료일
        assertThatThrownBy(() -> autoAssignService.autoAssignSupervisionSchedules(startDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작일과 종료일은 필수입니다.");
    }
}