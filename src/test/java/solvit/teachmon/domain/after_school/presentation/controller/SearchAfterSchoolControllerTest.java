package solvit.teachmon.domain.after_school.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import solvit.teachmon.domain.after_school.application.service.SearchAfterSchoolService;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolSearchResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 검색 컨트롤러 테스트")
class SearchAfterSchoolControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchAfterSchoolService searchAfterSchoolService;

    @InjectMocks
    private SearchAfterSchoolController searchAfterSchoolController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(searchAfterSchoolController).build();
    }

    @Test
    @DisplayName("방과후 이름으로 검색할 수 있다")
    void shouldSearchAfterSchoolByNameSuccessfully() throws Exception {
        // Given: 방과후 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "한국사";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후")
        );
        given(searchAfterSchoolService.searchAfterSchoolByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/afterschool/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("한국사 방과후"));

        verify(searchAfterSchoolService).searchAfterSchoolByQuery(query);
    }

    @Test
    @DisplayName("담당 선생님 이름으로 검색할 수 있다")
    void shouldSearchAfterSchoolByTeacherNameSuccessfully() throws Exception {
        // Given: 선생님 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "이혜정";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후")
        );
        given(searchAfterSchoolService.searchAfterSchoolByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/afterschool/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("한국사 방과후"));

        verify(searchAfterSchoolService).searchAfterSchoolByQuery(query);
    }

    @Test
    @DisplayName("부분 이름으로 검색할 수 있다")
    void shouldSearchAfterSchoolByPartialNameSuccessfully() throws Exception {
        // Given: 부분 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "이혜";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후")
        );
        given(searchAfterSchoolService.searchAfterSchoolByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/afterschool/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("한국사 방과후"));

        verify(searchAfterSchoolService).searchAfterSchoolByQuery(query);
    }

    @Test
    @DisplayName("여러 방과후가 검색될 수 있다")
    void shouldSearchMultipleAfterSchoolsSuccessfully() throws Exception {
        // Given: 여러 방과후가 매칭되는 검색 쿼리가 주어졌을 때
        String query = "방과후";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of(
                new AfterSchoolSearchResponseDto(1L, "한국사 방과후"),
                new AfterSchoolSearchResponseDto(2L, "수학 방과후")
        );
        given(searchAfterSchoolService.searchAfterSchoolByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 여러 검색 결과가 반환된다
        mockMvc.perform(get("/afterschool/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("한국사 방과후"))
                .andExpect(jsonPath("$[1].name").value("수학 방과후"));

        verify(searchAfterSchoolService).searchAfterSchoolByQuery(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() throws Exception {
        // Given: 매칭되는 방과후가 없는 검색 쿼리가 주어졌을 때
        String query = "없는방과후";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of();
        given(searchAfterSchoolService.searchAfterSchoolByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/afterschool/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchAfterSchoolService).searchAfterSchoolByQuery(query);
    }

    @Test
    @DisplayName("빈 쿼리로 방과후를 검색할 수 있다")
    void shouldSearchAfterSchoolByEmptyQuery() throws Exception {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<AfterSchoolSearchResponseDto> expectedResults = List.of();
        given(searchAfterSchoolService.searchAfterSchoolByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/afterschool/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchAfterSchoolService).searchAfterSchoolByQuery(query);
    }
}