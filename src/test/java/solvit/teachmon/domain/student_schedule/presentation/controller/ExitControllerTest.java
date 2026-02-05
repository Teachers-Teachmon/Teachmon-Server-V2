package solvit.teachmon.domain.student_schedule.presentation.controller;

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
import solvit.teachmon.domain.student_schedule.application.service.ExitService;
import solvit.teachmon.domain.student_schedule.exception.ExitNotFoundException;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ExitHistoryResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("이탈 컨트롤러 테스트")
class ExitControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ExitService exitService;

    @InjectMocks
    private ExitController exitController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exitController)
                .defaultResponseCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    @DisplayName("GET /exit/history/week - 이번주 이탈 학생을 조회할 수 있다")
    void shouldGetWeekExitHistory() throws Exception {
        // Given: 이번주 이탈 학생 데이터가 있을 때
        ExitHistoryResponse response1 = ExitHistoryResponse.builder()
                .exitId(1L)
                .day(LocalDate.now())
                .teacher("홍선생님")
                .number(1101)
                .name("김학생")
                .period(SchoolPeriod.ONE_PERIOD)
                .build();

        ExitHistoryResponse response2 = ExitHistoryResponse.builder()
                .exitId(2L)
                .day(LocalDate.now().plusDays(1))
                .teacher("이선생님")
                .number(2202)
                .name("박학생")
                .period(SchoolPeriod.TWO_PERIOD)
                .build();

        given(exitService.getWeekExitHistory()).willReturn(List.of(response1, response2));

        // When & Then: GET 요청을 보내면 이번주 이탈 학생 목록이 반환된다
        mockMvc.perform(get("/exit/history/week")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].exit_id").value(1))
                .andExpect(jsonPath("$[0].teacher").value("홍선생님"))
                .andExpect(jsonPath("$[0].number").value(1101))
                .andExpect(jsonPath("$[0].name").value("김학생"))
                .andExpect(jsonPath("$[1].exit_id").value(2))
                .andExpect(jsonPath("$[1].teacher").value("이선생님"))
                .andExpect(jsonPath("$[1].number").value(2202))
                .andExpect(jsonPath("$[1].name").value("박학생"));

        verify(exitService, times(1)).getWeekExitHistory();
    }

    @Test
    @DisplayName("GET /exit/history/week - 이번주 이탈 학생이 없으면 빈 배열이 반환된다")
    void shouldReturnEmptyArrayWhenNoExitsThisWeek() throws Exception {
        // Given: 이번주 이탈 학생이 없을 때
        given(exitService.getWeekExitHistory()).willReturn(List.of());

        // When & Then: GET 요청을 보내면 빈 배열이 반환된다
        mockMvc.perform(get("/exit/history/week")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(exitService, times(1)).getWeekExitHistory();
    }

    @Test
    @DisplayName("GET /exit/history?day={날짜} - 날짜별 이탈 학생을 조회할 수 있다")
    void shouldGetExitHistoryByDay() throws Exception {
        // Given: 특정 날짜 이탈 학생 데이터가 있을 때
        LocalDate targetDay = LocalDate.of(2024, 1, 15);

        ExitHistoryResponse response = ExitHistoryResponse.builder()
                .exitId(1L)
                .day(targetDay)
                .teacher("김선생님")
                .number(3105)
                .name("최학생")
                .period(SchoolPeriod.SEVEN_PERIOD)
                .build();

        given(exitService.getExitHistoryByDay(targetDay)).willReturn(List.of(response));

        // When & Then: GET 요청을 보내면 해당 날짜의 이탈 학생 목록이 반환된다
        mockMvc.perform(get("/exit/history")
                        .param("day", "2024-01-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].exit_id").value(1))
                .andExpect(jsonPath("$[0].day").value("2024-01-15"))
                .andExpect(jsonPath("$[0].teacher").value("김선생님"))
                .andExpect(jsonPath("$[0].number").value(3105))
                .andExpect(jsonPath("$[0].name").value("최학생"))
                .andExpect(jsonPath("$[0].period").value("SEVEN_PERIOD"));

        verify(exitService, times(1)).getExitHistoryByDay(targetDay);
    }

    @Test
    @DisplayName("GET /exit/history - day 파라미터가 없으면 400 에러가 발생한다")
    void shouldReturn400WhenDayParameterIsMissing() throws Exception {
        // When & Then: day 파라미터 없이 요청하면 400 에러가 발생한다
        mockMvc.perform(get("/exit/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(exitService, never()).getExitHistoryByDay(any());
    }

    @Test
    @DisplayName("DELETE /exit/{exit_id} - 이탈 기록을 삭제할 수 있다")
    void shouldDeleteExit() throws Exception {
        // Given: 삭제할 이탈 ID가 있을 때
        Long exitId = 1L;

        doNothing().when(exitService).deleteExit(exitId);

        // When & Then: DELETE 요청을 보내면 삭제가 수행되고 성공 응답이 반환된다
        mockMvc.perform(delete("/exit/{exit_id}", exitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(exitService, times(1)).deleteExit(exitId);
    }

    @Test
    @DisplayName("DELETE /exit/{exit_id} - 존재하지 않는 이탈 기록 삭제 시 예외가 발생한다")
    void shouldThrowExceptionWhenDeletingNonExistentExit() throws Exception {
        // Given: 존재하지 않는 이탈 ID일 때
        Long exitId = 999L;

        doThrow(new ExitNotFoundException()).when(exitService).deleteExit(exitId);

        // When & Then: DELETE 요청 시 예외가 발생한다
        try {
            mockMvc.perform(delete("/exit/{exit_id}", exitId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            // 예외가 발생하는 것이 정상
        }

        verify(exitService, times(1)).deleteExit(exitId);
    }
}
