package solvit.teachmon.domain.place.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import solvit.teachmon.domain.place.application.service.SearchPlaceService;
import solvit.teachmon.domain.place.presentation.dto.response.PlaceSearchResponseDto;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("장소 검색 컨트롤러 테스트")
class SearchPlaceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchPlaceService searchPlaceService;

    @InjectMocks
    private SearchPlaceController searchPlaceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchPlaceController).build();
    }

    @Test
    @DisplayName("이름으로 장소를 검색할 수 있다")
    void shouldSearchPlaceByNameSuccessfully() throws Exception {
        // Given: 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "인공지능 모델링실";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2)
        );
        given(searchPlaceService.searchPlaceByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/place/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(23423445326L))
                .andExpect(jsonPath("$[0].name").value("인공지능 모델링실"))
                .andExpect(jsonPath("$[0].floor").value(2));

        verify(searchPlaceService).searchPlaceByQuery(query);
    }

    @Test
    @DisplayName("부분 이름으로 장소를 검색할 수 있다")
    void shouldSearchPlaceByPartialNameSuccessfully() throws Exception {
        // Given: 부분 이름 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "모델링";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2),
                new PlaceSearchResponseDto(12345678901L, "3D 모델링실", 3)
        );
        given(searchPlaceService.searchPlaceByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/place/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("인공지능 모델링실"))
                .andExpect(jsonPath("$[1].name").value("3D 모델링실"));

        verify(searchPlaceService).searchPlaceByQuery(query);
    }

    @Test
    @DisplayName("층수로 장소를 검색할 수 있다")
    void shouldSearchPlaceByFloorSuccessfully() throws Exception {
        // Given: 층수 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "2";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2),
                new PlaceSearchResponseDto(98765432109L, "컴퓨터실1", 2)
        );
        given(searchPlaceService.searchPlaceByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/place/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].floor").value(2))
                .andExpect(jsonPath("$[1].floor").value(2));

        verify(searchPlaceService).searchPlaceByQuery(query);
    }

    @Test
    @DisplayName("검색 결과가 없을 수 있다")
    void shouldReturnEmptyResultWhenNoMatch() throws Exception {
        // Given: 매칭되는 장소가 없는 검색 쿼리가 주어졌을 때
        String query = "없는장소";
        List<PlaceSearchResponseDto> expectedResults = List.of();
        given(searchPlaceService.searchPlaceByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/place/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchPlaceService).searchPlaceByQuery(query);
    }

    @Test
    @DisplayName("빈 쿼리로 장소를 검색할 수 있다")
    void shouldSearchPlaceByEmptyQuery() throws Exception {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<PlaceSearchResponseDto> expectedResults = List.of();
        given(searchPlaceService.searchPlaceByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/place/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(searchPlaceService).searchPlaceByQuery(query);
    }

    @Test
    @DisplayName("대소문자 구분 없이 검색할 수 있다")
    void shouldSearchPlaceIgnoreCase() throws Exception {
        // Given: 대소문자가 다른 검색 쿼리가 주어졌을 때
        String query = "모델링실";
        List<PlaceSearchResponseDto> expectedResults = List.of(
                new PlaceSearchResponseDto(23423445326L, "인공지능 모델링실", 2)
        );
        given(searchPlaceService.searchPlaceByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/place/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("인공지능 모델링실"));

        verify(searchPlaceService).searchPlaceByQuery(query);
    }
}