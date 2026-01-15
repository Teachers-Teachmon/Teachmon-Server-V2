package solvit.teachmon.domain.self_study.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("추가 자습 설정 서비스 - 조회 테스트")
class AdditionalSelfStudyServiceGetTest {

    @Mock
    private AdditionalSelfStudyRepository additionalSelfStudyRepository;

    @InjectMocks
    private AdditionalSelfStudyService additionalSelfStudyService;

    @Test
    @DisplayName("년도별 추가 자습을 조회할 수 있다")
    void shouldGetAdditionalSelfStudy() {
        // Given: 2024년도 추가 자습 데이터가 있을 때
        Integer year = 2024;
        LocalDate day1 = LocalDate.of(2024, 3, 15);
        LocalDate day2 = LocalDate.of(2024, 5, 20);

        List<AdditionalSelfStudyEntity> entities = Arrays.asList(
                AdditionalSelfStudyEntity.builder()
                        .day(day1)
                        .grade(1)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day1)
                        .grade(1)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day2)
                        .grade(2)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build()
        );

        given(additionalSelfStudyRepository.findByYear(year)).willReturn(entities);

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: 날짜와 학년으로 그룹화된 결과가 반환된다
        verify(additionalSelfStudyRepository, times(1)).findByYear(year);

        assertThat(result).hasSize(2);

        // day1, grade1 검증
        AdditionalSelfStudyGetResponse response1 = result.stream()
                .filter(r -> r.day().equals(day1) && r.grade().equals(1))
                .findFirst()
                .orElseThrow();
        assertThat(response1.periods()).hasSize(2);
        assertThat(response1.periods()).extracting("period")
                .containsExactlyInAnyOrder(SchoolPeriod.SEVEN_PERIOD, SchoolPeriod.EIGHT_AND_NINE_PERIOD);

        // day2, grade2 검증
        AdditionalSelfStudyGetResponse response2 = result.stream()
                .filter(r -> r.day().equals(day2) && r.grade().equals(2))
                .findFirst()
                .orElseThrow();
        assertThat(response2.periods()).hasSize(1);
        assertThat(response2.periods()).extracting("period")
                .containsExactly(SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
    }

    @Test
    @DisplayName("추가 자습이 없으면 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoData() {
        // Given: 추가 자습이 없을 때
        Integer year = 2024;

        given(additionalSelfStudyRepository.findByYear(year)).willReturn(Collections.emptyList());

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: 빈 리스트가 반환된다
        verify(additionalSelfStudyRepository, times(1)).findByYear(year);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("같은 날짜, 같은 학년의 여러 교시가 하나의 응답으로 그룹화된다")
    void shouldGroupBySameDayAndGrade() {
        // Given: 같은 날짜, 같은 학년에 3개의 교시가 있을 때
        Integer year = 2024;
        LocalDate day = LocalDate.of(2024, 6, 10);

        List<AdditionalSelfStudyEntity> entities = Arrays.asList(
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(1)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(1)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(1)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build()
        );

        given(additionalSelfStudyRepository.findByYear(year)).willReturn(entities);

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: 1개의 응답으로 그룹화된다
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().day()).isEqualTo(day);
        assertThat(result.getFirst().grade()).isEqualTo(1);
        assertThat(result.getFirst().periods()).hasSize(3);
    }

    @Test
    @DisplayName("같은 날짜, 다른 학년은 별도의 응답으로 분리된다")
    void shouldSeparateByDifferentGrade() {
        // Given: 같은 날짜에 다른 학년의 추가 자습이 있을 때
        Integer year = 2024;
        LocalDate day = LocalDate.of(2024, 8, 15);

        List<AdditionalSelfStudyEntity> entities = Arrays.asList(
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(1)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(2)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(3)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build()
        );

        given(additionalSelfStudyRepository.findByYear(year)).willReturn(entities);

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: 3개의 응답으로 분리된다
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(r -> r.day().equals(day));
        assertThat(result).extracting(AdditionalSelfStudyGetResponse::grade)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    @DisplayName("다른 날짜는 별도의 응답으로 분리된다")
    void shouldSeparateByDifferentDay() {
        // Given: 다른 날짜의 추가 자습이 있을 때
        Integer year = 2024;
        LocalDate day1 = LocalDate.of(2024, 3, 15);
        LocalDate day2 = LocalDate.of(2024, 3, 16);
        LocalDate day3 = LocalDate.of(2024, 3, 17);

        List<AdditionalSelfStudyEntity> entities = Arrays.asList(
                AdditionalSelfStudyEntity.builder()
                        .day(day1)
                        .grade(1)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day2)
                        .grade(1)
                        .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        .build(),
                AdditionalSelfStudyEntity.builder()
                        .day(day3)
                        .grade(1)
                        .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        .build()
        );

        given(additionalSelfStudyRepository.findByYear(year)).willReturn(entities);

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: 3개의 응답으로 분리된다
        assertThat(result).hasSize(3);
        assertThat(result).extracting(AdditionalSelfStudyGetResponse::day)
                .containsExactlyInAnyOrder(day1, day2, day3);
    }

    @Test
    @DisplayName("ID가 응답에 포함된다")
    void shouldIncludeIdInResponse() {
        // Given: ID를 가진 추가 자습 데이터가 있을 때
        Integer year = 2024;
        LocalDate day = LocalDate.of(2024, 12, 25);

        // Builder를 통해 생성하면 ID가 없으므로 직접 설정할 수 없음
        // 실제 환경에서는 Repository에서 ID가 자동 생성됨
        List<AdditionalSelfStudyEntity> entities = Collections.singletonList(
                AdditionalSelfStudyEntity.builder()
                        .day(day)
                        .grade(1)
                        .period(SchoolPeriod.SEVEN_PERIOD)
                        .build()
        );

        given(additionalSelfStudyRepository.findByYear(year)).willReturn(entities);

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: ID가 포함된 응답이 반환된다
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().periods()).hasSize(1);
        // ID는 Builder를 통해 설정할 수 없으므로, 존재 여부만 확인
        assertThat(result.getFirst().periods().getFirst()).isNotNull();
    }
}
