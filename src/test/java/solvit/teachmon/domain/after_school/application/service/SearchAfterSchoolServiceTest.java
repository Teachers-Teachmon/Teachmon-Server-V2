package solvit.teachmon.domain.after_school.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolSearchResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 검색 서비스 테스트")
class SearchAfterSchoolServiceTest {

    @Mock
    private AfterSchoolRepository afterSchoolRepository;

    private SearchAfterSchoolService searchAfterSchoolService;

    @BeforeEach
    void setUp() {
        searchAfterSchoolService = new SearchAfterSchoolService(afterSchoolRepository);
    }

    @Test
    @DisplayName("쿼리로 방과후를 검색할 수 있다")
    void shouldSearchAfterSchoolByQuerySuccessfully() {
        // Given: 검색 쿼리가 주어졌을 때
        String query = "한국사";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후")
        );
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 검색 결과가 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(1L);
        assertThat(results.get(0).name()).isEqualTo("한국사 방과후");
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("담당 선생님 이름으로 방과후를 검색할 수 있다")
    void shouldSearchAfterSchoolByTeacherNameSuccessfully() {
        // Given: 선생님 이름 검색 쿼리가 주어졌을 때
        String query = "이혜정";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후")
        );
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 선생님 이름으로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 해당 선생님이 담당하는 방과후가 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(1L);
        assertThat(results.get(0).name()).isEqualTo("한국사 방과후");
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("부분 이름으로 방과후를 검색할 수 있다")
    void shouldSearchAfterSchoolByPartialNameSuccessfully() {
        // Given: 부분 이름 검색 쿼리가 주어졌을 때
        String query = "이혜";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후")
        );
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 부분 이름으로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 해당 이름이 포함된 방과후가 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("한국사 방과후");
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("여러 방과후가 검색될 수 있다")
    void shouldSearchMultipleAfterSchoolsSuccessfully() {
        // Given: 여러 방과후가 매칭되는 검색 쿼리가 주어졌을 때
        String query = "방과후";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후"),
                new AfterSchoolSearchResponseDto(2L, "수학 방과후")
        );
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 방과후들을 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 여러 방과후가 검색 결과로 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("한국사 방과후");
        assertThat(results.get(1).name()).isEqualTo("수학 방과후");
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() {
        // Given: 매칭되는 방과후가 없는 검색 쿼리가 주어졌을 때
        String query = "없는방과후";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of();
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("빈 쿼리로 검색할 수 있다")
    void shouldSearchWithEmptyQuery() {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of();
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 빈 쿼리로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("null 쿼리도 처리할 수 있다")
    void shouldHandleNullQuery() {
        // Given: null 쿼리가 주어졌을 때
        String query = null;
        List<AfterSchoolSearchResponseDto> expectedResults = List.of();
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: null 쿼리로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }

    @Test
    @DisplayName("과목명으로 방과후를 검색할 수 있다")
    void shouldSearchAfterSchoolBySubjectSuccessfully() {
        // Given: 과목명 검색 쿼리가 주어졌을 때
        String query = "수학";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(2L, "수학 방과후"),
                new AfterSchoolSearchResponseDto(3L, "고급 수학 방과후")
        );
        given(afterSchoolRepository.searchAfterSchoolsByKeyword(query)).willReturn(expectedResults);

        // When: 과목명으로 방과후를 검색하면
        List<AfterSchoolSearchResponseDto> results = searchAfterSchoolService.searchAfterSchoolByQuery(query);

        // Then: 해당 과목의 방과후들이 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).contains("수학");
        assertThat(results.get(1).name()).contains("수학");
        verify(afterSchoolRepository).searchAfterSchoolsByKeyword(query);
    }
}