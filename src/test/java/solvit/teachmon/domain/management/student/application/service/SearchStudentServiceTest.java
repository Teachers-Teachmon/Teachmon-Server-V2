package solvit.teachmon.domain.management.student.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.presentation.dto.response.StudentSearchResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 검색 서비스 테스트")
class SearchStudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    private SearchStudentService searchStudentService;

    @BeforeEach
    void setUp() {
        searchStudentService = new SearchStudentService(studentRepository);
    }

    @Test
    @DisplayName("쿼리로 학생을 검색할 수 있다")
    void shouldSearchStudentByQuerySuccessfully() {
        // Given: 검색 쿼리가 주어졌을 때
        String query = "김동욱";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱")
        );
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 검색 결과가 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(483858324L);
        assertThat(results.get(0).grade()).isEqualTo(2);
        assertThat(results.get(0).classNumber()).isEqualTo(2);
        assertThat(results.get(0).number()).isEqualTo(3);
        assertThat(results.get(0).name()).isEqualTo("김동욱");
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("학번으로 학생을 검색할 수 있다")
    void shouldSearchStudentByStudentNumberSuccessfully() {
        // Given: 학번 검색 쿼리가 주어졌을 때
        String query = "2203";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱")
        );
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 학번으로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 해당 학번의 학생이 반환된다
        assertThat(results).hasSize(1);
        assertThat(results.get(0).grade()).isEqualTo(2);
        assertThat(results.get(0).classNumber()).isEqualTo(2);
        assertThat(results.get(0).number()).isEqualTo(3);
        assertThat(results.get(0).name()).isEqualTo("김동욱");
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("여러 학생이 검색될 수 있다")
    void shouldSearchMultipleStudentsSuccessfully() {
        // Given: 여러 학생이 매칭되는 검색 쿼리가 주어졌을 때
        String query = "김";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱"),
                new StudentSearchResponseDto(247725234L, 1, 1, 5, "김철수")
        );
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 학생들을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 여러 학생이 검색 결과로 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("김동욱");
        assertThat(results.get(1).name()).isEqualTo("김철수");
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() {
        // Given: 매칭되는 학생이 없는 검색 쿼리가 주어졌을 때
        String query = "없는학생";
        List<StudentSearchResponseDto> expectedResults = List.of();
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("빈 쿼리로 검색할 수 있다")
    void shouldSearchWithEmptyQuery() {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<StudentSearchResponseDto> expectedResults = List.of();
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 빈 쿼리로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("null 쿼리도 처리할 수 있다")
    void shouldHandleNullQuery() {
        // Given: null 쿼리가 주어졌을 때
        String query = null;
        List<StudentSearchResponseDto> expectedResults = List.of();
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: null 쿼리로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 빈 결과가 반환된다
        assertThat(results).isEmpty();
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("숫자와 문자 혼합 쿼리로 검색할 수 있다")
    void shouldSearchWithMixedQuery() {
        // Given: 숫자와 문자가 혼합된 검색 쿼리가 주어졌을 때
        String query = "220김";
        List<StudentSearchResponseDto> expectedResults = List.of();
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 혼합 쿼리로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 결과가 정상적으로 처리된다
        assertThat(results).isEmpty();
        verify(studentRepository).searchStudentsByKeyword(query);
    }

    @Test
    @DisplayName("부분 번호로 학생을 검색할 수 있다")
    void shouldSearchStudentByPartialNumberSuccessfully() {
        // Given: 부분 번호 검색 쿼리가 주어졌을 때
        String query = "3";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱"),
                new StudentSearchResponseDto(987654321L, 1, 3, 13, "박영희")
        );
        given(studentRepository.searchStudentsByKeyword(query)).willReturn(expectedResults);

        // When: 부분 번호로 학생을 검색하면
        List<StudentSearchResponseDto> results = searchStudentService.searchStudentByQuery(query);

        // Then: 해당 번호가 포함된 학생들이 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).number()).isEqualTo(3);
        assertThat(results.get(1).number()).isEqualTo(13);
        verify(studentRepository).searchStudentsByKeyword(query);
    }
}