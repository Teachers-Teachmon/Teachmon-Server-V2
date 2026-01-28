package solvit.teachmon.domain.team.presentation.controller;

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
import solvit.teachmon.domain.team.application.service.TeamService;
import solvit.teachmon.domain.team.presentation.dto.request.TeamCreateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamDeleteRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateStudentDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("팀 컨트롤러 테스트")
class TeamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
    }

    @Test
    @DisplayName("쿼리로 팀을 검색할 수 있다")
    void shouldSearchTeamByQuerySuccessfully() throws Exception {
        // Given: 검색 쿼리와 예상 결과가 주어졌을 때
        String query = "개발";
        List<TeamResponseDto> expectedResults = List.of(
                new TeamResponseDto(1L, "개발팀"),
                new TeamResponseDto(2L, "개발부서")
        );
        given(teamService.searchTeamByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 검색 결과가 반환된다
        mockMvc.perform(get("/team/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("개발팀"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("개발부서"));

        verify(teamService).searchTeamByQuery(query);
    }

    @Test
    @DisplayName("빈 쿼리로 팀을 검색할 수 있다")
    void shouldSearchTeamByEmptyQuery() throws Exception {
        // Given: 빈 쿼리가 주어졌을 때
        String query = "";
        List<TeamResponseDto> expectedResults = List.of();
        given(teamService.searchTeamByQuery(query)).willReturn(expectedResults);

        // When & Then: GET 요청을 보내면 빈 결과가 반환된다
        mockMvc.perform(get("/team/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(teamService).searchTeamByQuery(query);
    }

    @Test
    @DisplayName("팀을 생성할 수 있다")
    void shouldCreateTeamSuccessfully() throws Exception {
        // Given: 팀 생성 요청이 주어졌을 때
        TeamCreateRequestDto requestDto = new TeamCreateRequestDto("새로운팀", List.of(1L, 2L));
        willDoNothing().given(teamService).createTeam(any());

        // When & Then: POST 요청을 보내면 팀이 생성된다
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(teamService).createTeam(any(TeamCreateRequestDto.class));
    }

    @Test
    @DisplayName("학생이 없는 팀을 생성할 수 있다")
    void shouldCreateTeamWithoutStudentsSuccessfully() throws Exception {
        // Given: 학생이 없는 팀 생성 요청이 주어졌을 때
        TeamCreateRequestDto requestDto = new TeamCreateRequestDto("빈팀", List.of());
        willDoNothing().given(teamService).createTeam(any());

        // When & Then: POST 요청을 보내면 팀이 생성된다
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(teamService).createTeam(any(TeamCreateRequestDto.class));
    }

    @Test
    @DisplayName("팀 정보를 수정할 수 있다")
    void shouldUpdateTeamSuccessfully() throws Exception {
        // Given: 팀 수정 요청이 주어졌을 때
        List<TeamUpdateStudentDto> students = List.of(
                new TeamUpdateStudentDto(1L, 1, "김철수"),
                new TeamUpdateStudentDto(2L, 2, "이영희")
        );
        TeamUpdateRequestDto requestDto = new TeamUpdateRequestDto(1L, "수정된팀명", students);
        willDoNothing().given(teamService).updateTeam(any());

        // When & Then: PATCH 요청을 보내면 팀이 수정된다
        mockMvc.perform(patch("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(teamService).updateTeam(any(TeamUpdateRequestDto.class));
    }

    @Test
    @DisplayName("팀을 삭제할 수 있다")
    void shouldDeleteTeamSuccessfully() throws Exception {
        // Given: 팀 삭제 요청이 주어졌을 때
        TeamDeleteRequestDto requestDto = new TeamDeleteRequestDto(1L);
        willDoNothing().given(teamService).deleteTeam(any());

        // When & Then: DELETE 요청을 보내면 팀이 삭제된다
        mockMvc.perform(delete("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(teamService).deleteTeam(any(TeamDeleteRequestDto.class));
    }
}
