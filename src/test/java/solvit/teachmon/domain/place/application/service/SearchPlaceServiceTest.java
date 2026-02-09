package solvit.teachmon.domain.place.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.place.presentation.dto.response.PlaceSearchResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("장소 검색 서비스 테스트")
class SearchPlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    private SearchPlaceService searchPlaceService;

    @BeforeEach
    void setUp() {
        searchPlaceService = new SearchPlaceService(placeRepository);
    }

    @Test
    @DisplayName("쿼리로 장소를 검색할 수 있다")
    void shouldSearchPlaceByQuerySuccessfully() {
        // Given: 검색 쿼리가 주어졌을 때
        String query = "인공지능 모델링실";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2)
        );
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 검색 결과가 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(23423445326L);
        assertThat(results.get(0).name()).isEqualTo("인공지능 모델링실");
        assertThat(results.get(0).floor()).isEqualTo(2);
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("부분 이름으로 장소를 검색할 수 있다")
    void shouldSearchPlaceByPartialNameSuccessfully() {
        // Given: 부분 이름 검색 쿼리가 주어졌을 때
        String query = "모델링";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2),
                new PlaceSearchResponseDto(12345678901L, "3D 모델링실", 3)
        );
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 부분 이름으로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 해당 이름이 포함된 장소들이 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("인공지능 모델링실");
        assertThat(results.get(1).name()).isEqualTo("3D 모델링실");
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("층수로 장소를 검색할 수 있다")
    void shouldSearchPlaceByFloorSuccessfully() {
        // Given: 층수 검색 쿼리가 주어졌을 때
        String query = "2";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2),
                new PlaceSearchResponseDto(98765432109L, "컴퓨터실1", 2)
        );
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 층수로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 해당 층수의 장소들이 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).floor()).isEqualTo(2);
        assertThat(results.get(1).floor()).isEqualTo(2);
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() {
        // Given: 매칭되는 장소가 없는 검색 쿼리가 주어졌을 때
        String query = "없는장소";
        List<PlaceSearchResponseDto> expectedResults = List.of();
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("빈 쿼리로 검색할 수 있다")
    void shouldSearchWithEmptyQuery() {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<PlaceSearchResponseDto> expectedResults = List.of();
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 빈 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("null 쿼리도 처리할 수 있다")
    void shouldHandleNullQuery() {
        // Given: null 쿼리가 주어졌을 때
        String query = null;
        List<PlaceSearchResponseDto> expectedResults = List.of();
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: null 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("대소문자 구분 없이 검색할 수 있다")
    void shouldSearchIgnoreCase() {
        // Given: 대소문자가 다른 검색 쿼리가 주어졌을 때
        String query = "모델링실";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2)
        );
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 대소문자가 다른 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 검색 결과가 정상적으로 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("인공지능 모델링실");
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("특수 문자가 포함된 쿼리를 처리할 수 있다")
    void shouldHandleSpecialCharacters() {
        // Given: 특수 문자가 포함된 검색 쿼리가 주어졌을 때
        String query = "실1-2";
        List<PlaceSearchResponseDto> expectedResults = List.of();
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 특수 문자가 포함된 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 결과가 정상적으로 처리된다
        assertThat(results).isEmpty();
        verify(placeRepository).searchPlacesByKeyword(query);
    }

    @Test
    @DisplayName("숫자와 문자 혼합 쿼리를 처리할 수 있다")
    void shouldHandleMixedQuery() {
        // Given: 숫자와 문자가 혼합된 검색 쿼리가 주어졌을 때
        String query = "2층 실습실";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(99999999999L, "2층 실습실", 2)
        );
        given(placeRepository.searchPlacesByKeyword(query)).willReturn(expectedResults);

        // When: 혼합 쿼리로 장소를 검색하면
        List<PlaceSearchResponseDto> results = searchPlaceService.searchPlaceByQuery(query);

        // Then: 결과가 정상적으로 처리된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("2층 실습실");
        verify(placeRepository).searchPlacesByKeyword(query);
    }
}