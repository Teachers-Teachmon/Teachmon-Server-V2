package solvit.teachmon.domain.after_school.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolByTeacherResponseDto;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("방과후 선생님별 조회 서비스 테스트")
class AfterSchoolServiceTeacherTest {

    @InjectMocks
    private AfterSchoolService afterSchoolService;

    @Mock
    private AfterSchoolStudentDomainService afterSchoolStudentDomainService;
    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;
    @Mock
    private AfterSchoolRepository afterSchoolRepository;
    @Mock
    private AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;

    private TeacherEntity teacher;
    private BranchEntity branch;
    private PlaceEntity place;
    private AfterSchoolEntity afterSchool1;
    private AfterSchoolEntity afterSchool2;
    private Long teacherId;

    @BeforeEach
    void setUp() {
        teacherId = 1L;
        
        teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("teacher@test.com")
                .providerId("provider123")
                .build();
        
        branch = BranchEntity.builder()
                .year(2025)
                .branch(1)
                .startDay(LocalDate.of(2025, 3, 1))
                .endDay(LocalDate.of(2025, 12, 31))
                .build();
        
        place = mock(PlaceEntity.class);
        when(place.getId()).thenReturn(101L);
        when(place.getName()).thenReturn("수학실");
        
        afterSchool1 = mock(AfterSchoolEntity.class);
        when(afterSchool1.getId()).thenReturn(1L);
        when(afterSchool1.getName()).thenReturn("수학 방과후");
        when(afterSchool1.getWeekDay()).thenReturn(WeekDay.MON);
        when(afterSchool1.getPeriod()).thenReturn(SchoolPeriod.SEVEN_PERIOD);
        when(afterSchool1.getPlace()).thenReturn(place);
        when(afterSchool1.getIsEnd()).thenReturn(false);
        
        PlaceEntity place2 = mock(PlaceEntity.class);
        when(place2.getId()).thenReturn(102L);
        when(place2.getName()).thenReturn("영어실");
        
        afterSchool2 = mock(AfterSchoolEntity.class);
        when(afterSchool2.getId()).thenReturn(2L);
        when(afterSchool2.getName()).thenReturn("영어 방과후");
        when(afterSchool2.getWeekDay()).thenReturn(WeekDay.TUE);
        when(afterSchool2.getPeriod()).thenReturn(SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        when(afterSchool2.getPlace()).thenReturn(place2);
        when(afterSchool2.getIsEnd()).thenReturn(false);
    }

    @Test
    @DisplayName("선생님 ID로 방과후를 조회할 수 있다")
    void shouldGetAfterSchoolsByTeacherId() {
        // Given
        List<AfterSchoolEntity> afterSchools = List.of(afterSchool1, afterSchool2);
        given(afterSchoolRepository.findByTeacherIdWithRelations(teacherId))
                .willReturn(afterSchools);
        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of());

        // When
        List<AfterSchoolByTeacherResponseDto> result = afterSchoolService.getAfterSchoolsByTeacherId(teacherId);

        // Then
        assertThat(result).hasSize(2);
        
        AfterSchoolByTeacherResponseDto first = result.get(0);
        assertThat(first.id()).isEqualTo(1L);
        assertThat(first.weekDay()).isEqualTo("월");
        assertThat(first.period()).isEqualTo("7교시");
        assertThat(first.name()).isEqualTo("수학 방과후");
        assertThat(first.place().name()).isEqualTo("수학실");
        assertThat(first.reinforcementCount()).isEqualTo(0);
        
        AfterSchoolByTeacherResponseDto second = result.get(1);
        assertThat(second.id()).isEqualTo(2L);
        assertThat(second.weekDay()).isEqualTo("화");
        assertThat(second.period()).isEqualTo("8~9교시");
        assertThat(second.name()).isEqualTo("영어 방과후");
        assertThat(second.place().name()).isEqualTo("영어실");
        assertThat(second.reinforcementCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("선생님 ID로 조회 시 보강 횟수가 포함된다")
    void shouldIncludeReinforcementCount() {
        // Given
        List<AfterSchoolEntity> afterSchools = List.of(afterSchool1);
        AfterSchoolReinforcementEntity reinforcement1 = createReinforcement(afterSchool1, LocalDate.now().plusDays(5));
        AfterSchoolReinforcementEntity reinforcement2 = createReinforcement(afterSchool1, LocalDate.now().plusDays(3));
        
        given(afterSchoolRepository.findByTeacherIdWithRelations(teacherId))
                .willReturn(afterSchools);
        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of(reinforcement1, reinforcement2));

        // When
        List<AfterSchoolByTeacherResponseDto> result = afterSchoolService.getAfterSchoolsByTeacherId(teacherId);

        // Then
        assertThat(result).hasSize(1);
        AfterSchoolByTeacherResponseDto dto = result.get(0);
        assertThat(dto.reinforcementCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("해당 선생님의 방과후가 없으면 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoAfterSchools() {
        // Given
        given(afterSchoolRepository.findByTeacherIdWithRelations(teacherId))
                .willReturn(List.of());

        // When
        List<AfterSchoolByTeacherResponseDto> result = afterSchoolService.getAfterSchoolsByTeacherId(teacherId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 방과후의 보강은 카운트되지 않는다")
    void shouldNotCountReinforcementsFromOtherAfterSchools() {
        // Given
        List<AfterSchoolEntity> afterSchools = List.of(afterSchool1);
        AfterSchoolReinforcementEntity reinforcementForThis = createReinforcement(afterSchool1, LocalDate.now().plusDays(3));
        AfterSchoolReinforcementEntity reinforcementForOther = createReinforcement(afterSchool2, LocalDate.now().plusDays(2));
        
        given(afterSchoolRepository.findByTeacherIdWithRelations(teacherId))
                .willReturn(afterSchools);
        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of(reinforcementForThis, reinforcementForOther));

        // When
        List<AfterSchoolByTeacherResponseDto> result = afterSchoolService.getAfterSchoolsByTeacherId(teacherId);

        // Then
        assertThat(result).hasSize(1);
        AfterSchoolByTeacherResponseDto dto = result.get(0);
        assertThat(dto.reinforcementCount()).isEqualTo(1); // afterSchool1의 보강만 카운트
    }

    private AfterSchoolReinforcementEntity createReinforcement(AfterSchoolEntity afterSchool, LocalDate changeDay) {
        return AfterSchoolReinforcementEntity.builder()
                .afterSchool(afterSchool)
                .changeDay(changeDay)
                .place(place)
                .changePeriod(SchoolPeriod.SEVEN_PERIOD)
                .build();
    }
}