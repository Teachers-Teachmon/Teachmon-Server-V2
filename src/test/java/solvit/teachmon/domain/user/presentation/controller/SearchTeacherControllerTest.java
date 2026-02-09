package solvit.teachmon.domain.user.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import solvit.teachmon.domain.user.application.service.SearchTeacherService;
import solvit.teachmon.domain.user.presentation.dto.response.TeacherSearchResponseDto;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 검색 컨트롤러 테스트")
class SearchTeacherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchTeacherService searchTeacherService;

    @InjectMocks
    private SearchTeacherController searchTeacherController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchTeacherController).build();
    }

    @Test
    @DisplayName("이름으로 선생님을 검색할 수 있다")
    void shouldSearchTeacherByNameSuccessfully() throws Exception {
        // Given: 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "이혜정";
        List<TeacherSearchResponseDto> expectedResults = List.of(
                new TeacherSearchResponseDto(234235326L, "이혜정")
        );
        given(searchTeacherService.searchTeacherByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/teacher/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(234235326L))
                .andExpect(jsonPath("$[0].name").value("이혜정"));

        verify(searchTeacherService).searchTeacherByQuery(query);
    }

    @Test
    @DisplayName("부분 이름으로 선생님을 검색할 수 있다")
    void shouldSearchTeacherByPartialNameSuccessfully() throws Exception {
        // Given: 부분 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "혜정";
        List<TeacherSearchResponseDto> expectedResults = List.of(
                new TeacherSearchResponseDto(234235326L, "이혜정"),
                new TeacherSearchResponseDto(543543553L, "윤혜정")
        );
        given(searchTeacherService.searchTeacherByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/teacher/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(234235326L))
                .andExpect(jsonPath("$[0].name").value("이혜정"))
                .andExpect(jsonPath("$[1].id").value(543543553L))
                .andExpect(jsonPath("$[1].name").value("윤혜정"));

        verify(searchTeacherService).searchTeacherByQuery(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() throws Exception {
        // Given: 매칭되는 선생님이 없는 검색 쿼리가 주어졌을 때
        String query = "없는선생님";
        List<TeacherSearchResponseDto> expectedResults = List.of();
        given(searchTeacherService.searchTeacherByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/teacher/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchTeacherService).searchTeacherByQuery(query);
    }

    @Test
    @DisplayName("빈 쿼리로 선생님을 검색할 수 있다")
    void shouldSearchTeacherByEmptyQuery() throws Exception {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<TeacherSearchResponseDto> expectedResults = List.of();
        given(searchTeacherService.searchTeacherByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/teacher/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchTeacherService).searchTeacherByQuery(query);
    }

    @Test
    @DisplayName("대소문자 구분 없이 검색할 수 있다")
    void shouldSearchTeacherIgnoreCase() throws Exception {
        // Given: 대소문자가 다른 검색 쿼리가 주어졌을 때
        String query = "혜정";
        List<TeacherSearchResponseDto> expectedResults = List.of(
                new TeacherSearchResponseDto(234235326L, "이혜정")
        );
        given(searchTeacherService.searchTeacherByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/teacher/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("이혜정"));

        verify(searchTeacherService).searchTeacherByQuery(query);
    }
}