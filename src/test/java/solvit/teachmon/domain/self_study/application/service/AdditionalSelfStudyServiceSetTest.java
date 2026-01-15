package solvit.teachmon.domain.self_study.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("추가 자습 설정 서비스 - 설정 테스트")
class AdditionalSelfStudyServiceSetTest {

    @Mock
    private AdditionalSelfStudyRepository additionalSelfStudyRepository;

    @InjectMocks
    private AdditionalSelfStudyService additionalSelfStudyService;

    @Captor
    private ArgumentCaptor<List<AdditionalSelfStudyEntity>> entitiesCaptor;

    @Test
    @DisplayName("추가 자습을 설정할 수 있다")
    void shouldSetAdditionalSelfStudy() {
        // Given: 날짜, 학년, 교시 정보가 있을 때
        LocalDate day = LocalDate.of(2024, 3, 15);
        Integer grade = 1;
        List<SchoolPeriod> periods = Arrays.asList(SchoolPeriod.SEVEN_PERIOD, SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        AdditionalSelfStudySetRequest request = new AdditionalSelfStudySetRequest(day, grade, periods);

        // When: 추가 자습을 설정하면
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        // Then: 교시 개수만큼 엔티티가 저장된다
        verify(additionalSelfStudyRepository, times(1)).saveAll(entitiesCaptor.capture());

        List<AdditionalSelfStudyEntity> savedEntities = entitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities).allMatch(e -> e.getDay().equals(day));
        assertThat(savedEntities).allMatch(e -> e.getGrade().equals(grade));
        assertThat(savedEntities).extracting(AdditionalSelfStudyEntity::getPeriod)
                .containsExactlyInAnyOrder(SchoolPeriod.SEVEN_PERIOD, SchoolPeriod.EIGHT_AND_NINE_PERIOD);
    }

    @Test
    @DisplayName("한 교시만 추가 자습으로 설정할 수 있다")
    void shouldSetSinglePeriod() {
        // Given: 한 교시만 있을 때
        LocalDate day = LocalDate.of(2024, 5, 20);
        Integer grade = 2;
        List<SchoolPeriod> periods = Collections.singletonList(SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
        AdditionalSelfStudySetRequest request = new AdditionalSelfStudySetRequest(day, grade, periods);

        // When: 추가 자습을 설정하면
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        // Then: 1개의 엔티티가 저장된다
        verify(additionalSelfStudyRepository, times(1)).saveAll(entitiesCaptor.capture());

        List<AdditionalSelfStudyEntity> savedEntities = entitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(1);
        assertThat(savedEntities.getFirst().getDay()).isEqualTo(day);
        assertThat(savedEntities.getFirst().getGrade()).isEqualTo(grade);
        assertThat(savedEntities.getFirst().getPeriod()).isEqualTo(SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
    }

    @Test
    @DisplayName("모든 교시를 추가 자습으로 설정할 수 있다")
    void shouldSetAllPeriods() {
        // Given: 모든 교시가 있을 때
        LocalDate day = LocalDate.of(2024, 6, 10);
        Integer grade = 3;
        List<SchoolPeriod> periods = Arrays.asList(
                SchoolPeriod.SEVEN_PERIOD,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                SchoolPeriod.TEN_AND_ELEVEN_PERIOD
        );
        AdditionalSelfStudySetRequest request = new AdditionalSelfStudySetRequest(day, grade, periods);

        // When: 추가 자습을 설정하면
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        // Then: 3개의 엔티티가 저장된다
        verify(additionalSelfStudyRepository, times(1)).saveAll(entitiesCaptor.capture());

        List<AdditionalSelfStudyEntity> savedEntities = entitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(3);
        assertThat(savedEntities).extracting(AdditionalSelfStudyEntity::getPeriod)
                .containsExactlyInAnyOrder(
                        SchoolPeriod.SEVEN_PERIOD,
                        SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                        SchoolPeriod.TEN_AND_ELEVEN_PERIOD
                );
    }

    @Test
    @DisplayName("빈 교시 리스트로 추가 자습을 설정하면 아무것도 저장되지 않는다")
    void shouldNotSaveWhenPeriodsEmpty() {
        // Given: 빈 교시 리스트가 있을 때
        LocalDate day = LocalDate.of(2024, 7, 1);
        Integer grade = 1;
        List<SchoolPeriod> periods = Collections.emptyList();
        AdditionalSelfStudySetRequest request = new AdditionalSelfStudySetRequest(day, grade, periods);

        // When: 추가 자습을 설정하면
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        // Then: 빈 리스트가 저장된다
        verify(additionalSelfStudyRepository, times(1)).saveAll(entitiesCaptor.capture());

        List<AdditionalSelfStudyEntity> savedEntities = entitiesCaptor.getValue();
        assertThat(savedEntities).isEmpty();
    }

    @Test
    @DisplayName("다양한 학년에 대해 추가 자습을 설정할 수 있다")
    void shouldSetForDifferentGrades() {
        // Given: 1학년 추가 자습 정보가 있을 때
        LocalDate day = LocalDate.of(2024, 8, 15);
        Integer grade = 1;
        List<SchoolPeriod> periods = List.of(SchoolPeriod.SEVEN_PERIOD);
        AdditionalSelfStudySetRequest request = new AdditionalSelfStudySetRequest(day, grade, periods);

        // When: 추가 자습을 설정하면
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        // Then: 1학년 추가 자습이 저장된다
        verify(additionalSelfStudyRepository, times(1)).saveAll(entitiesCaptor.capture());

        List<AdditionalSelfStudyEntity> savedEntities = entitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(1);
        assertThat(savedEntities.getFirst().getGrade()).isEqualTo(1);
    }

    @Test
    @DisplayName("다양한 날짜에 추가 자습을 설정할 수 있다")
    void shouldSetForDifferentDates() {
        // Given: 특정 날짜의 추가 자습 정보가 있을 때
        LocalDate day = LocalDate.of(2024, 12, 25);
        Integer grade = 2;
        List<SchoolPeriod> periods = List.of(SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        AdditionalSelfStudySetRequest request = new AdditionalSelfStudySetRequest(day, grade, periods);

        // When: 추가 자습을 설정하면
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        // Then: 해당 날짜로 추가 자습이 저장된다
        verify(additionalSelfStudyRepository, times(1)).saveAll(entitiesCaptor.capture());

        List<AdditionalSelfStudyEntity> savedEntities = entitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(1);
        assertThat(savedEntities.getFirst().getDay()).isEqualTo(LocalDate.of(2024, 12, 25));
    }
}
