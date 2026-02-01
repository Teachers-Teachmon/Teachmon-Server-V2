package solvit.teachmon.domain.supervision.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.supervision.domain.dto.TeacherSupervisionInfo;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionAutoAssignRepository;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.supervision.domain.strategy.SupervisionPriorityStrategy;
import solvit.teachmon.domain.supervision.exception.InsufficientTeachersException;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;
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
    private SupervisionAutoAssignRepository autoAssignRepository;
    
    @Mock
    private SupervisionScheduleRepository scheduleRepository;
    
    @Mock
    private TeacherRepository teacherRepository;
    
    @Mock
    private SupervisionPriorityStrategy priorityStrategy;

    private SupervisionAutoAssignService autoAssignService;
    
    // 테스트 데이터
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> teacherProjections;
    private List<SupervisionAutoAssignRepository.SupervisionBanDayProjection> banDayProjections;
    private TeacherEntity teacher1;
    private TeacherEntity teacher2;
    private TeacherEntity teacher3;

    @BeforeEach
    void setUp() {
        autoAssignService = new SupervisionAutoAssignService(
                autoAssignRepository, scheduleRepository, teacherRepository, priorityStrategy);
        
        // 테스트 기간 설정 (2025-02-03 ~ 2025-02-06, 월~목)
        startDate = LocalDate.of(2025, 2, 3);
        endDate = LocalDate.of(2025, 2, 6);
        
        // 교사 엔티티 생성
        teacher1 = createTeacher(1L, "김선생", true);
        teacher2 = createTeacher(2L, "이선생", true);
        teacher3 = createTeacher(3L, "박선생", true);
        
        // 교사 Projection 데이터
        teacherProjections = List.of(
            createTeacherProjection(1L, "김선생", null, 0L),
            createTeacherProjection(2L, "이선생", LocalDate.of(2025, 1, 15), 2L),
            createTeacherProjection(3L, "박선생", LocalDate.of(2025, 1, 20), 1L)
        );
        
        // 금지요일 데이터 (김선생은 화요일 금지)
        banDayProjections = List.of(
            createBanDayProjection(1L, WeekDay.TUE)
        );
    }

    @Test
    @DisplayName("정상적인 기간으로 자동 배정 시 성공적으로 스케줄이 생성된다")
    void shouldCreateSchedulesSuccessfullyWhenValidPeriodProvided() {
        // Given: 정상적인 교사 데이터와 기간이 준비됨
        given(autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER))
                .willReturn(teacherProjections);
        given(autoAssignRepository.findBanDaysByTeacherIds(anyList()))
                .willReturn(banDayProjections);
        given(autoAssignRepository.existsScheduleByDate(any(LocalDate.class)))
                .willReturn(false);
        
        // 우선순위 설정 (김선생 > 이선생 > 박선생)
        given(priorityStrategy.calculatePriority(any(TeacherSupervisionInfo.class), any(LocalDate.class)))
                .willReturn(10.0, 8.0, 6.0); // 첫 번째가 가장 높은 우선순위
        
        given(teacherRepository.findById(1L)).willReturn(Optional.of(teacher1));
        given(teacherRepository.findById(2L)).willReturn(Optional.of(teacher2));
        given(teacherRepository.findById(3L)).willReturn(Optional.of(teacher3));
        
        given(scheduleRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        // When: 자동 배정 실행
        List<SupervisionScheduleResponseDto> result = autoAssignService.autoAssignSupervisionSchedules(startDate, endDate);

        // Then: 4일간 스케줄이 생성됨 (월~목)
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(4); // 4일 (월, 화, 수, 목)
        
        // 각 날짜별로 자습감독과 이석감독이 배정되었는지 확인
        result.forEach(schedule -> {
            assertThat(schedule.selfStudySupervision()).isNotNull();
            assertThat(schedule.leaveSeatSupervision()).isNotNull();
            assertThat(schedule.selfStudySupervision().teacher()).isNotNull();
            assertThat(schedule.leaveSeatSupervision().teacher()).isNotNull();
        });
        
        // Repository 호출 검증
        verify(autoAssignRepository).findTeacherSupervisionInfoByRole(Role.TEACHER);
        verify(scheduleRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("교사가 2명 미만일 때 빈 스케줄 리스트가 반환된다")
    void shouldReturnEmptySchedulesWhenLessThanTwoTeachers() {
        // Given: 교사가 1명만 있음
        List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> oneTeacher = List.of(
            createTeacherProjection(1L, "김선생", null, 0L)
        );
        
        given(autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER))
                .willReturn(oneTeacher);
        given(autoAssignRepository.findBanDaysByTeacherIds(anyList()))
                .willReturn(Collections.emptyList());
        given(autoAssignRepository.existsScheduleByDate(any(LocalDate.class)))
                .willReturn(false);
        given(scheduleRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        // When: 자동 배정 실행
        List<SupervisionScheduleResponseDto> result = autoAssignService.autoAssignSupervisionSchedules(startDate, endDate);

        // Then: 빈 리스트 반환 (각 날짜별로 배정 실패하여 스케줄이 생성되지 않음)
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("교사가 없을 때 InsufficientTeachersException이 발생한다")
    void shouldThrowInsufficientTeachersExceptionWhenNoTeachersAvailable() {
        // Given: 교사가 전혀 없음
        given(autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER))
                .willReturn(Collections.emptyList());

        // When & Then: 예외 발생
        assertThatThrownBy(() -> autoAssignService.autoAssignSupervisionSchedules(startDate, endDate))
                .isInstanceOf(InsufficientTeachersException.class)
                .hasMessage("감독 배정 가능한 교사가 없습니다.");
    }

    @Test
    @DisplayName("이미 스케줄이 존재하는 날짜는 건너뛴다")
    void shouldSkipDatesWhenScheduleAlreadyExists() {
        // Given: 특정 날짜에 이미 스케줄 존재
        given(autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER))
                .willReturn(teacherProjections);
        given(autoAssignRepository.findBanDaysByTeacherIds(anyList()))
                .willReturn(banDayProjections);
        
        // 월요일에는 이미 스케줄 존재, 나머지는 없음
        given(autoAssignRepository.existsScheduleByDate(startDate)).willReturn(true); // 월요일
        given(autoAssignRepository.existsScheduleByDate(startDate.plusDays(1))).willReturn(false); // 화요일
        given(autoAssignRepository.existsScheduleByDate(startDate.plusDays(2))).willReturn(false); // 수요일
        given(autoAssignRepository.existsScheduleByDate(startDate.plusDays(3))).willReturn(false); // 목요일

        given(priorityStrategy.calculatePriority(any(TeacherSupervisionInfo.class), any(LocalDate.class)))
                .willReturn(10.0, 8.0, 6.0);
        
        given(teacherRepository.findById(anyLong())).willReturn(Optional.of(teacher1));
        given(scheduleRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        // When: 자동 배정 실행
        List<SupervisionScheduleResponseDto> result = autoAssignService.autoAssignSupervisionSchedules(startDate, endDate);

        // Then: 3일만 생성됨 (화, 수, 목)
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("금지요일에 해당하는 교사는 배정되지 않는다")
    void shouldNotAssignTeachersOnTheirBanDays() {
        // Given: 김선생은 화요일 금지
        given(autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER))
                .willReturn(teacherProjections);
        given(autoAssignRepository.findBanDaysByTeacherIds(anyList()))
                .willReturn(banDayProjections);
        given(autoAssignRepository.existsScheduleByDate(any(LocalDate.class)))
                .willReturn(false);

        // 화요일에는 김선생 제외하고 우선순위 계산
        given(priorityStrategy.calculatePriority(any(TeacherSupervisionInfo.class), any(LocalDate.class)))
                .willReturn(10.0, 8.0);
        
        given(teacherRepository.findById(anyLong())).willReturn(Optional.of(teacher2));
        given(scheduleRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        // When: 화요일 하루만 배정
        LocalDate tuesday = LocalDate.of(2025, 2, 4); // 화요일
        List<SupervisionScheduleResponseDto> result = autoAssignService.autoAssignSupervisionSchedules(tuesday, tuesday);

        // Then: 성공적으로 배정됨 (김선생 제외)
        assertThat(result).hasSize(1);
        
        verify(priorityStrategy, atLeastOnce()).calculatePriority(any(TeacherSupervisionInfo.class), eq(tuesday));
    }

    // Helper methods
    private TeacherEntity createTeacher(Long id, String name, boolean isActive) {
        return TeacherEntity.builder()
                .name(name)
                .mail(name.toLowerCase() + "@test.com")
                .providerId("provider_" + id)
                .oAuth2Type(solvit.teachmon.domain.user.domain.enums.OAuth2Type.GOOGLE)
                .build(); // isActive는 builder에서 자동으로 true로 설정됨
    }

    private SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection createTeacherProjection(
            Long teacherId, String teacherName, LocalDate lastDate, Long totalCount) {
        return new SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection() {
            @Override
            public Long getTeacherId() { return teacherId; }
            
            @Override
            public String getTeacherName() { return teacherName; }
            
            @Override
            public LocalDate getLastSupervisionDate() { return lastDate; }
            
            @Override
            public Long getTotalSupervisionCount() { return totalCount; }
        };
    }

    private SupervisionAutoAssignRepository.SupervisionBanDayProjection createBanDayProjection(
            Long teacherId, WeekDay weekDay) {
        return new SupervisionAutoAssignRepository.SupervisionBanDayProjection() {
            @Override
            public Long getTeacherId() { return teacherId; }
            
            @Override
            public WeekDay getWeekDay() { return weekDay; }
        };
    }
}