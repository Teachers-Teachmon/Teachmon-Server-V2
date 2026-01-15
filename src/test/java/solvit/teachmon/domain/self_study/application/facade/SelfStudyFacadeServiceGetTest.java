package solvit.teachmon.domain.self_study.application.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.common.WeekDaySelfStudyDto;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("자습 설정 조회 서비스 테스트")
class SelfStudyFacadeServiceGetTest {

    @Mock
    private SelfStudyRepository selfStudyRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SelfStudyFacadeService selfStudyFacadeService;

    @Test
    @DisplayName("자습 설정을 조회할 수 있다")
    void shouldGetSelfStudy() {
        // Given: 분기와 자습 설정이 존재할 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<SelfStudyEntity> selfStudyEntities = Arrays.asList(
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.MON)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.MON)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.TUE)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build()
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));
        given(selfStudyRepository.findAllByYearAndBranchAndGrade(year, branchEntity, grade))
                .willReturn(selfStudyEntities);

        // When: 자습 설정을 조회하면
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branchNumber, grade);

        // Then: 모든 요일이 포함되어 반환된다
        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, times(1)).findAllByYearAndBranchAndGrade(year, branchEntity, grade);

        assertThat(result).hasSize(4);  // 모든 요일 (MON~THU)

        // MON 검증 (2개의 교시)
        WeekDaySelfStudyDto monDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.MON)
                .findFirst()
                .orElseThrow();
        assertThat(monDto.periods()).hasSize(2);
        assertThat(monDto.periods()).containsExactlyInAnyOrder(
                SchoolPeriod.SEVEN_PERIOD,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD
        );

        // TUE 검증 (1개의 교시)
        WeekDaySelfStudyDto tueDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.TUE)
                .findFirst()
                .orElseThrow();
        assertThat(tueDto.periods()).hasSize(1);
        assertThat(tueDto.periods()).containsExactly(SchoolPeriod.SEVEN_PERIOD);

        // WED, THU 검증 (빈 교시)
        WeekDaySelfStudyDto wedDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.WED)
                .findFirst()
                .orElseThrow();
        assertThat(wedDto.periods()).isEmpty();

        WeekDaySelfStudyDto thuDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.THU)
                .findFirst()
                .orElseThrow();
        assertThat(thuDto.periods()).isEmpty();
    }

    @Test
    @DisplayName("자습 설정이 없으면 모든 요일이 빈 periods로 반환된다")
    void shouldReturnAllWeekdaysWithEmptyPeriodsWhenNoSelfStudy() {
        // Given: 분기는 존재하지만 자습 설정이 없을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));
        given(selfStudyRepository.findAllByYearAndBranchAndGrade(year, branchEntity, grade))
                .willReturn(Collections.emptyList());

        // When: 자습 설정을 조회하면
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branchNumber, grade);

        // Then: 모든 요일이 빈 periods로 반환된다
        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, times(1)).findAllByYearAndBranchAndGrade(year, branchEntity, grade);

        assertThat(result).hasSize(4);  // 모든 요일 (MON~THU)
        assertThat(result).allMatch(dto -> dto.periods().isEmpty());  // 모든 요일의 periods가 비어있음
    }

    @Test
    @DisplayName("존재하지 않는 분기로 자습 조회 시 예외가 발생한다")
    void shouldThrowExceptionWhenBranchNotFound() {
        // Given: 존재하지 않는 분기 정보가 있을 때
        Integer year = 2024;
        Integer branchNumber = 999;
        Integer grade = 1;

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.empty());

        // When & Then: 자습 설정을 조회하면 예외가 발생한다
        assertThatThrownBy(() -> selfStudyFacadeService.getSelfStudy(year, branchNumber, grade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 분기를 찾을 수 없습니다. 분기 설정을 먼저 해주세요");

        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, never()).findAllByYearAndBranchAndGrade(any(), any(), any());
    }

    @Test
    @DisplayName("특정 요일에만 여러 교시가 설정된 경우 정상 조회된다")
    void shouldGetSelfStudyWithMultiplePeriodsInOneDay() {
        // Given: 월요일에만 3개의 교시가 설정된 경우
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 2;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<SelfStudyEntity> selfStudyEntities = Arrays.asList(
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.MON)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.MON)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.MON)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build()
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));
        given(selfStudyRepository.findAllByYearAndBranchAndGrade(year, branchEntity, grade))
                .willReturn(selfStudyEntities);

        // When: 자습 설정을 조회하면
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branchNumber, grade);

        // Then: 월요일에 3개의 교시가 포함되어 반환된다
        assertThat(result).hasSize(4);

        WeekDaySelfStudyDto monDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.MON)
                .findFirst()
                .orElseThrow();
        assertThat(monDto.periods()).hasSize(3);
        assertThat(monDto.periods()).containsExactlyInAnyOrder(
                SchoolPeriod.SEVEN_PERIOD,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                SchoolPeriod.TEN_AND_ELEVEN_PERIOD
        );

        // 나머지 요일은 빈 리스트
        assertThat(result.stream()
                .filter(dto -> dto.weekDay() != WeekDay.MON)
                .allMatch(dto -> dto.periods().isEmpty())).isTrue();
    }

    @Test
    @DisplayName("모든 요일에 자습이 설정된 경우 정상 조회된다")
    void shouldGetSelfStudyForAllWeekdays() {
        // Given: 모든 요일에 자습이 설정된 경우
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<SelfStudyEntity> selfStudyEntities = Arrays.asList(
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.MON)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.TUE)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.WED)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.THU)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build()
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));
        given(selfStudyRepository.findAllByYearAndBranchAndGrade(year, branchEntity, grade))
                .willReturn(selfStudyEntities);

        // When: 자습 설정을 조회하면
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branchNumber, grade);

        // Then: 모든 요일에 자습이 포함되어 반환된다
        assertThat(result).hasSize(4);
        assertThat(result).allMatch(dto -> !dto.periods().isEmpty());

        // 각 요일별 검증
        assertThat(result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.MON)
                .findFirst().orElseThrow().periods())
                .containsExactly(SchoolPeriod.SEVEN_PERIOD);

        assertThat(result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.TUE)
                .findFirst().orElseThrow().periods())
                .containsExactly(SchoolPeriod.EIGHT_AND_NINE_PERIOD);

        assertThat(result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.WED)
                .findFirst().orElseThrow().periods())
                .containsExactly(SchoolPeriod.TEN_AND_ELEVEN_PERIOD);

        assertThat(result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.THU)
                .findFirst().orElseThrow().periods())
                .containsExactly(SchoolPeriod.SEVEN_PERIOD);
    }

    @Test
    @DisplayName("다양한 학년에 대해 자습 설정을 조회할 수 있다")
    void shouldGetSelfStudyForDifferentGrades() {
        // Given: 3학년 자습 설정이 존재할 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 3;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<SelfStudyEntity> selfStudyEntities = Collections.singletonList(
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.WED)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build()
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));
        given(selfStudyRepository.findAllByYearAndBranchAndGrade(year, branchEntity, grade))
                .willReturn(selfStudyEntities);

        // When: 자습 설정을 조회하면
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branchNumber, grade);

        // Then: 수요일에만 자습이 포함되어 반환된다
        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, times(1)).findAllByYearAndBranchAndGrade(year, branchEntity, grade);

        assertThat(result).hasSize(4);

        WeekDaySelfStudyDto wedDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.WED)
                .findFirst()
                .orElseThrow();
        assertThat(wedDto.periods()).hasSize(1);
        assertThat(wedDto.periods()).containsExactly(SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
    }

    @Test
    @DisplayName("다른 분기의 자습 설정을 조회할 수 있다")
    void shouldGetSelfStudyForDifferentBranch() {
        // Given: 2분기 자습 설정이 존재할 때
        Integer year = 2024;
        Integer branchNumber = 2;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<SelfStudyEntity> selfStudyEntities = Arrays.asList(
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.THU)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                SelfStudyEntity.builder()
                        .year(year)
                        .branch(branchEntity)
                        .grade(grade)
                        .weekDay(WeekDay.THU)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build()
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));
        given(selfStudyRepository.findAllByYearAndBranchAndGrade(year, branchEntity, grade))
                .willReturn(selfStudyEntities);

        // When: 자습 설정을 조회하면
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branchNumber, grade);

        // Then: 목요일에 2개의 교시가 포함되어 반환된다
        assertThat(result).hasSize(4);

        WeekDaySelfStudyDto thuDto = result.stream()
                .filter(dto -> dto.weekDay() == WeekDay.THU)
                .findFirst()
                .orElseThrow();
        assertThat(thuDto.periods()).hasSize(2);
        assertThat(thuDto.periods()).containsExactlyInAnyOrder(
                SchoolPeriod.SEVEN_PERIOD,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD
        );
    }
}
