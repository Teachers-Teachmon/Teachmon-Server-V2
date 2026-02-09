package solvit.teachmon.domain.management.student.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import solvit.teachmon.domain.management.student.application.service.SearchStudentService;
import solvit.teachmon.domain.management.student.presentation.dto.response.StudentSearchResponseDto;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 검색 컨트롤러 테스트")
class SearchStudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchStudentService searchStudentService;

    @InjectMocks
    private SearchStudentController searchStudentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchStudentController).build();
    }

    @Test
    @DisplayName("이름으로 학생을 검색할 수 있다")
    void shouldSearchStudentByNameSuccessfully() throws Exception {
        // Given: 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "김동욱";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱")
        );
        given(searchStudentService.searchStudentByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/student/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(483858324L))
                .andExpect(jsonPath("$[0].grade").value(2))
                .andExpect(jsonPath("$[0].classNumber").value(2))
                .andExpect(jsonPath("$[0].number").value(3))
                .andExpect(jsonPath("$[0].name").value("김동욱"));

        verify(searchStudentService).searchStudentByQuery(query);
    }

    @Test
    @DisplayName("학번으로 학생을 검색할 수 있다")
    void shouldSearchStudentByStudentNumberSuccessfully() throws Exception {
        // Given: 학번 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "2203";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱")
        );
        given(searchStudentService.searchStudentByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/student/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(483858324L))
                .andExpect(jsonPath("$[0].grade").value(2))
                .andExpect(jsonPath("$[0].classNumber").value(2))
                .andExpect(jsonPath("$[0].number").value(3))
                .andExpect(jsonPath("$[0].name").value("김동욱"));

        verify(searchStudentService).searchStudentByQuery(query);
    }

    @Test
    @DisplayName("부분 학번으로 학생을 검색할 수 있다")
    void shouldSearchStudentByPartialNumberSuccessfully() throws Exception {
        // Given: 부분 학번 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "3";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱")
        );
        given(searchStudentService.searchStudentByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/student/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value(3));

        verify(searchStudentService).searchStudentByQuery(query);
    }

    @Test
    @DisplayName("여러 학생이 검색될 수 있다")
    void shouldSearchMultipleStudentsSuccessfully() throws Exception {
        // Given: 여러 학생이 매칭되는 검색 쿼리가 주어졌을 때
        String query = "김";
        List<StudentSearchResponseDto> expectedResults = List.of(
                new StudentSearchResponseDto(483858324L, 2, 2, 3, "김동욱"),
                new StudentSearchResponseDto(247725234L, 1, 1, 5, "김철수")
        );
        given(searchStudentService.searchStudentByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 여러 검색 결과가 반환된다
        mockMvc.perform(get("/student/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("김동욱"))
                .andExpect(jsonPath("$[1].name").value("김철수"));

        verify(searchStudentService).searchStudentByQuery(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() throws Exception {
        // Given: 매칭되는 학생이 없는 검색 쿼리가 주어졌을 때
        String query = "없는학생";
        List<StudentSearchResponseDto> expectedResults = List.of();
        given(searchStudentService.searchStudentByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/student/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchStudentService).searchStudentByQuery(query);
    }

    @Test
    @DisplayName("빈 쿼리로 학생을 검색할 수 있다")
    void shouldSearchStudentByEmptyQuery() throws Exception {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<StudentSearchResponseDto> expectedResults = List.of();
        given(searchStudentService.searchStudentByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/student/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchStudentService).searchStudentByQuery(query);
    }
}