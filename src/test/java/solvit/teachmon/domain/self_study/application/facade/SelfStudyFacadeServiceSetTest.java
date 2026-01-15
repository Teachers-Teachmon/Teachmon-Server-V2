package solvit.teachmon.domain.self_study.application.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
@DisplayName("자습 설정 서비스 테스트")
class SelfStudyFacadeServiceSetTest {

    @Mock
    private SelfStudyRepository selfStudyRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SelfStudyFacadeService selfStudyFacadeService;

    @Captor
    private ArgumentCaptor<List<SelfStudyEntity>> selfStudyEntitiesCaptor;

    @Test
    @DisplayName("자습을 설정할 수 있다")
    void shouldSetSelfStudy() {
        // Given: 분기가 존재하고, 자습 설정 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Arrays.asList(
                new WeekDaySelfStudyDto(WeekDay.MON, Arrays.asList(SchoolPeriod.SEVEN_PERIOD)),
                new WeekDaySelfStudyDto(WeekDay.TUE, Arrays.asList(SchoolPeriod.EIGHT_AND_NINE_PERIOD))
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 기존 자습이 삭제되고 새로운 자습이 저장된다
        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, times(1)).deleteAllByBranchAndGrade(branchEntity, grade);
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities).extracting(SelfStudyEntity::getWeekDay)
                .containsExactlyInAnyOrder(WeekDay.MON, WeekDay.TUE);
        assertThat(savedEntities).extracting(SelfStudyEntity::getPeriod)
                .containsExactlyInAnyOrder(SchoolPeriod.SEVEN_PERIOD, SchoolPeriod.EIGHT_AND_NINE_PERIOD);
    }

    @Test
    @DisplayName("여러 교시를 한 요일에 설정할 수 있다")
    void shouldSetMultiplePeriodsInOneDay() {
        // Given: 분기가 존재하고, 한 요일에 여러 교시 설정 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 2;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Collections.singletonList(
                new WeekDaySelfStudyDto(WeekDay.WED, Arrays.asList(
                        SchoolPeriod.SEVEN_PERIOD,
                        SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                        SchoolPeriod.TEN_AND_ELEVEN_PERIOD
                ))
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 3개의 교시가 저장된다
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(3);
        assertThat(savedEntities).allMatch(e -> e.getWeekDay().equals(WeekDay.WED));
        assertThat(savedEntities).extracting(SelfStudyEntity::getPeriod)
                .containsExactlyInAnyOrder(
                        SchoolPeriod.SEVEN_PERIOD,
                        SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                        SchoolPeriod.TEN_AND_ELEVEN_PERIOD
                );
    }

    @Test
    @DisplayName("중복된 교시는 제거된다")
    void shouldRemoveDuplicatePeriods() {
        // Given: 분기가 존재하고, 중복된 교시가 포함된 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Collections.singletonList(
                new WeekDaySelfStudyDto(WeekDay.THU, Arrays.asList(
                        SchoolPeriod.SEVEN_PERIOD,
                        SchoolPeriod.SEVEN_PERIOD,
                        SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                        SchoolPeriod.SEVEN_PERIOD
                ))
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 중복이 제거되어 2개의 교시만 저장된다
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities).extracting(SelfStudyEntity::getPeriod)
                .containsExactlyInAnyOrder(SchoolPeriod.SEVEN_PERIOD, SchoolPeriod.EIGHT_AND_NINE_PERIOD);
    }

    @Test
    @DisplayName("빈 리스트로 자습을 설정하면 기존 자습만 삭제된다")
    void shouldDeleteAllWhenEmptyList() {
        // Given: 분기가 존재하고, 빈 자습 설정 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 3;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Collections.emptyList();

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 빈 리스트로 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 기존 자습이 삭제되고 새로운 자습은 저장되지 않는다
        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, times(1)).deleteAllByBranchAndGrade(branchEntity, grade);
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 분기로 자습 설정 시 예외가 발생한다")
    void shouldThrowExceptionWhenBranchNotFound() {
        // Given: 존재하지 않는 분기 정보가 있을 때
        Integer year = 2024;
        Integer branchNumber = 999;
        Integer grade = 1;

        List<WeekDaySelfStudyDto> requests = Collections.singletonList(
                new WeekDaySelfStudyDto(WeekDay.MON, Arrays.asList(SchoolPeriod.SEVEN_PERIOD))
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.empty());

        // When & Then: 자습을 설정하면 예외가 발생한다
        assertThatThrownBy(() -> selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 분기를 찾을 수 없습니다. 분기 설정을 먼저 해주세요");

        verify(branchRepository, times(1)).findByYearAndBranch(year, branchNumber);
        verify(selfStudyRepository, never()).deleteAllByBranchAndGrade(any(), any());
        verify(selfStudyRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("모든 요일에 자습을 설정할 수 있다")
    void shouldSetAllWeekdays() {
        // Given: 분기가 존재하고, 모든 요일에 자습 설정 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Arrays.asList(
                new WeekDaySelfStudyDto(WeekDay.MON, Arrays.asList(SchoolPeriod.SEVEN_PERIOD)),
                new WeekDaySelfStudyDto(WeekDay.TUE, Arrays.asList(SchoolPeriod.SEVEN_PERIOD)),
                new WeekDaySelfStudyDto(WeekDay.WED, Arrays.asList(SchoolPeriod.SEVEN_PERIOD)),
                new WeekDaySelfStudyDto(WeekDay.THU, Arrays.asList(SchoolPeriod.SEVEN_PERIOD))
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 모든 요일에 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 4개의 자습이 저장된다
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(4);
        assertThat(savedEntities).extracting(SelfStudyEntity::getWeekDay)
                .containsExactlyInAnyOrder(WeekDay.MON, WeekDay.TUE, WeekDay.WED, WeekDay.THU);
    }

    @Test
    @DisplayName("다양한 학년에 대해 자습을 설정할 수 있다")
    void shouldSetSelfStudyForDifferentGrades() {
        // Given: 분기가 존재하고, 3학년 자습 설정 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 3;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Collections.singletonList(
                new WeekDaySelfStudyDto(WeekDay.THU, Arrays.asList(SchoolPeriod.TEN_AND_ELEVEN_PERIOD))
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 3학년 자습이 저장된다
        verify(selfStudyRepository, times(1)).deleteAllByBranchAndGrade(branchEntity, grade);
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(1);
        assertThat(savedEntities.getFirst().getGrade()).isEqualTo(3);
    }

    @Test
    @DisplayName("빈 교시 리스트가 포함된 요일은 저장되지 않는다")
    void shouldNotSaveWeekdayWithEmptyPeriods() {
        // Given: 분기가 존재하고, 빈 교시 리스트가 포함된 요청이 있을 때
        Integer year = 2024;
        Integer branchNumber = 1;
        Integer grade = 1;
        BranchEntity branchEntity = mock(BranchEntity.class);

        List<WeekDaySelfStudyDto> requests = Arrays.asList(
                new WeekDaySelfStudyDto(WeekDay.MON, Arrays.asList(SchoolPeriod.SEVEN_PERIOD)),
                new WeekDaySelfStudyDto(WeekDay.TUE, Collections.emptyList())  // 빈 교시 리스트
        );

        given(branchRepository.findByYearAndBranch(year, branchNumber)).willReturn(Optional.of(branchEntity));

        // When: 자습을 설정하면
        selfStudyFacadeService.setSelfStudy(year, branchNumber, grade, requests);

        // Then: 빈 교시 리스트가 있는 요일은 저장되지 않는다
        verify(selfStudyRepository, times(1)).saveAll(selfStudyEntitiesCaptor.capture());

        List<SelfStudyEntity> savedEntities = selfStudyEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(1);
        assertThat(savedEntities.getFirst().getWeekDay()).isEqualTo(WeekDay.MON);
    }
}
