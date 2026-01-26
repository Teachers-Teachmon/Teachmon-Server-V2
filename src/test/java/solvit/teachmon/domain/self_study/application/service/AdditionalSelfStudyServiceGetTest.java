package solvit.teachmon.domain.self_study.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyPeriodResponse;
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
    @DisplayName("Repository에서 조회한 데이터를 그대로 반환한다")
    void shouldReturnDataFromRepository() {
        // Given: Repository에서 그룹화된 데이터를 반환할 때
        Integer year = 2024;
        LocalDate day1 = LocalDate.of(2024, 3, 15);
        LocalDate day2 = LocalDate.of(2024, 5, 20);

        List<AdditionalSelfStudyGetResponse> mockResponses = Arrays.asList(
                new AdditionalSelfStudyGetResponse(
                        day1,
                        1,
                        Arrays.asList(
                                new AdditionalSelfStudyPeriodResponse(1L, SchoolPeriod.SEVEN_PERIOD),
                                new AdditionalSelfStudyPeriodResponse(2L, SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                        )
                ),
                new AdditionalSelfStudyGetResponse(
                        day2,
                        2,
                        Collections.singletonList(
                                new AdditionalSelfStudyPeriodResponse(3L, SchoolPeriod.TEN_AND_ELEVEN_PERIOD)
                        )
                )
        );

        given(additionalSelfStudyRepository.findGroupedByDayAndGrade(year)).willReturn(mockResponses);

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: Repository 메서드가 호출되고, 결과가 그대로 반환된다
        verify(additionalSelfStudyRepository, times(1)).findGroupedByDayAndGrade(year);
        assertThat(result).isEqualTo(mockResponses);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("추가 자습이 없으면 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoData() {
        // Given: Repository에서 빈 리스트를 반환할 때
        Integer year = 2024;

        given(additionalSelfStudyRepository.findGroupedByDayAndGrade(year)).willReturn(Collections.emptyList());

        // When: 년도별 추가 자습을 조회하면
        List<AdditionalSelfStudyGetResponse> result = additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: Repository 메서드가 호출되고, 빈 리스트가 반환된다
        verify(additionalSelfStudyRepository, times(1)).findGroupedByDayAndGrade(year);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("올바른 year 파라미터로 Repository를 호출한다")
    void shouldCallRepositoryWithCorrectYear() {
        // Given: 특정 년도가 주어질 때
        Integer year = 2025;
        List<AdditionalSelfStudyGetResponse> mockResponses = Collections.emptyList();

        given(additionalSelfStudyRepository.findGroupedByDayAndGrade(year)).willReturn(mockResponses);

        // When: 해당 년도의 추가 자습을 조회하면
        additionalSelfStudyService.getAdditionalSelfStudy(year);

        // Then: Repository가 올바른 year 파라미터로 호출된다
        verify(additionalSelfStudyRepository, times(1)).findGroupedByDayAndGrade(year);
    }
}
