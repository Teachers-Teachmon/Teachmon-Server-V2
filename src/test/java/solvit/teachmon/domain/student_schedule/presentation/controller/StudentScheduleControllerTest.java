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
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleService;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.exception.StudentScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleUpdateRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.HistoryStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.StudentScheduleResponse;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 컨트롤러 테스트")
class StudentScheduleControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private StudentScheduleService studentScheduleService;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private StudentScheduleController studentScheduleController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentScheduleController)
                .defaultResponseCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    @DisplayName("GET /student-schedule - 학년별 학생 스케줄을 조회할 수 있다")
    void shouldGetGradeStudentSchedules() throws Exception {
        // Given: 학년, 날짜, 교시 파라미터가 있을 때
        Integer grade = 1;
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = SchoolPeriod.ONE_PERIOD;

        ClassStudentScheduleResponse response1 = ClassStudentScheduleResponse.builder()
                .classNumber(1)
                .students(List.of(
                        StudentScheduleResponse.builder()
                                .studentId(1L)
                                .number(1)
                                .name("김학생")
                                .state(ScheduleType.SELF_STUDY)
                                .scheduleId(1L)
                                .build()
                ))
                .build();

        ClassStudentScheduleResponse response2 = ClassStudentScheduleResponse.builder()
                .classNumber(2)
                .students(List.of(
                        StudentScheduleResponse.builder()
                                .studentId(2L)
                                .number(1)
                                .name("이학생")
                                .state(ScheduleType.AWAY)
                                .scheduleId(2L)
                                .build()
                ))
                .build();

        given(studentScheduleService.getGradeStudentSchedules(grade, day, period))
                .willReturn(List.of(response1, response2));

        // When & Then: GET 요청을 보내면 학년별 스케줄이 반환된다
        mockMvc.perform(get("/student-schedule")
                        .param("grade", String.valueOf(grade))
                        .param("day", day.toString())
                        .param("period", period.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].class").value(1))
                .andExpect(jsonPath("$[0].students[0].name").value("김학생"))
                .andExpect(jsonPath("$[1].class").value(2))
                .andExpect(jsonPath("$[1].students[0].name").value("이학생"));

        verify(studentScheduleService).getGradeStudentSchedules(grade, day, period);
    }

    @Test
    @DisplayName("GET /student-schedule - grade 파라미터가 없으면 400 에러를 반환한다")
    void shouldReturn400WhenGradeIsMissing() throws Exception {
        // When & Then: grade 파라미터 없이 요청하면 400 에러가 발생한다
        mockMvc.perform(get("/student-schedule")
                        .param("day", "2024-01-15")
                        .param("period", "ONE_PERIOD"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /student-schedule - day 파라미터가 없으면 400 에러를 반환한다")
    void shouldReturn400WhenDayIsMissing() throws Exception {
        // When & Then: day 파라미터 없이 요청하면 400 에러가 발생한다
        mockMvc.perform(get("/student-schedule")
                        .param("grade", "1")
                        .param("period", "ONE_PERIOD"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /student-schedule - period 파라미터가 없으면 400 에러를 반환한다")
    void shouldReturn400WhenPeriodIsMissing() throws Exception {
        // When & Then: period 파라미터 없이 요청하면 400 에러가 발생한다
        mockMvc.perform(get("/student-schedule")
                        .param("grade", "1")
                        .param("day", "2024-01-15"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /student-schedule/{scheduleId} - 학생 스케줄을 수정할 수 있다")
    void shouldUpdateStudentSchedule() throws Exception {
        // Given: 스케줄 ID와 변경 요청이 있을 때
        Long scheduleId = 1L;
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.AWAY);

        TeacherEntity teacher = TeacherEntity.builder()
                .name("테스트 선생님")
                .mail("test@teacher.com")
                .profile("테스트")
                .build();
        given(teacherRepository.findById(anyLong())).willReturn(Optional.of(teacher));

        // When & Then: PATCH 요청을 보내면 스케줄이 수정된다
        mockMvc.perform(patch("/student-schedule/{scheduleId}", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(studentScheduleService).updateStudentSchedule(eq(scheduleId), any(StudentScheduleUpdateRequest.class), any());
    }

    @Test
    @DisplayName("PATCH /student-schedule/{scheduleId} - state가 없으면 400 에러를 반환한다")
    void shouldReturn400WhenStateIsMissing() throws Exception {
        // Given: state가 없는 요청이 있을 때
        Long scheduleId = 1L;
        String invalidRequest = "{}";

        // When & Then: 유효하지 않은 요청을 보내면 400 에러가 발생한다
        mockMvc.perform(patch("/student-schedule/{scheduleId}", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /student-schedule/{scheduleId} - 존재하지 않는 스케줄 ID로 요청하면 서비스에서 예외가 발생한다")
    void shouldThrowExceptionWhenScheduleNotFound() throws Exception {
        // Given: 존재하지 않는 스케줄 ID가 있을 때
        Long nonExistentId = 999L;
        StudentScheduleUpdateRequest request = new StudentScheduleUpdateRequest(ScheduleType.AWAY);

        TeacherEntity teacher = TeacherEntity.builder()
                .name("테스트 선생님")
                .mail("test@teacher.com")
                .profile("테스트")
                .build();
        given(teacherRepository.findById(anyLong())).willReturn(Optional.of(teacher));
        doThrow(new StudentScheduleNotFoundException())
                .when(studentScheduleService)
                .updateStudentSchedule(eq(nonExistentId), any(StudentScheduleUpdateRequest.class), any());

        // When & Then: 존재하지 않는 ID로 요청하면 예외가 발생한다
        // standaloneSetup에서는 @ControllerAdvice가 없기 때문에 ServletException으로 래핑됩니다
        try {
            mockMvc.perform(patch("/student-schedule/{scheduleId}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print());
        } catch (Exception e) {
            // StudentScheduleNotFoundException이 발생하면 성공
            assertThat(e.getCause()).isInstanceOf(StudentScheduleNotFoundException.class);
        }

        verify(studentScheduleService).updateStudentSchedule(eq(nonExistentId), any(StudentScheduleUpdateRequest.class), any());
    }

    @Test
    @DisplayName("GET /student-schedule/history - 학생 스케줄 히스토리를 조회할 수 있다")
    void shouldGetStudentScheduleHistory() throws Exception {
        // Given: 학생 쿼리와 날짜가 있을 때
        String query = "2115";
        LocalDate day = LocalDate.of(2024, 1, 15);

        HistoryStudentScheduleResponse response = HistoryStudentScheduleResponse.builder()
                .studentNumber(2115)
                .name("허온")
                .onePeriod(ScheduleType.SELF_STUDY)
                .twoPeriod(ScheduleType.SELF_STUDY)
                .threePeriod(ScheduleType.AWAY)
                .fourPeriod(null)
                .fivePeriod(null)
                .sixPeriod(null)
                .sevenPeriod(ScheduleType.SELF_STUDY)
                .eightAndNinePeriod(ScheduleType.SELF_STUDY)
                .tenAndElevenPeriod(ScheduleType.AFTER_SCHOOL)
                .build();

        given(studentScheduleService.getStudentScheduleHistory(query, day))
                .willReturn(List.of(response));

        // When & Then: GET 요청을 보내면 히스토리가 반환된다
        mockMvc.perform(get("/student-schedule/history")
                        .param("query", query)
                        .param("day", day.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student_number").value(2115))
                .andExpect(jsonPath("$[0].name").value("허온"))
                .andExpect(jsonPath("$[0].ONE_PERIOD").value("SELF_STUDY"))
                .andExpect(jsonPath("$[0].TWO_PERIOD").value("SELF_STUDY"))
                .andExpect(jsonPath("$[0].THREE_PERIOD").value("AWAY"))
                .andExpect(jsonPath("$[0].SEVEN_PERIOD").value("SELF_STUDY"))
                .andExpect(jsonPath("$[0].EIGHT_AND_NINE_PERIOD").value("SELF_STUDY"))
                .andExpect(jsonPath("$[0].TEN_AND_ELEVEN_PERIOD").value("AFTER_SCHOOL"));

        verify(studentScheduleService).getStudentScheduleHistory(query, day);
    }

    @Test
    @DisplayName("GET /student-schedule/history - day 파라미터가 없으면 400 에러를 반환한다")
    void shouldReturn400WhenDayIsMissingInHistory() throws Exception {
        // When & Then: day 파라미터 없이 요청하면 400 에러가 발생한다
        mockMvc.perform(get("/student-schedule/history")
                        .param("query", "2115"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /student-schedule/history - 빈 쿼리로 조회할 수 있다")
    void shouldGetHistoryWithEmptyQuery() throws Exception {
        // Given: 빈 쿼리와 날짜가 있을 때
        String query = "";
        LocalDate day = LocalDate.of(2024, 1, 15);

        HistoryStudentScheduleResponse response1 = HistoryStudentScheduleResponse.builder()
                .studentNumber(1101).name("학생1")
                .onePeriod(ScheduleType.SELF_STUDY).build();
        HistoryStudentScheduleResponse response2 = HistoryStudentScheduleResponse.builder()
                .studentNumber(1102).name("학생2")
                .onePeriod(ScheduleType.AWAY).build();

        given(studentScheduleService.getStudentScheduleHistory(query, day))
                .willReturn(List.of(response1, response2));

        // When & Then: 빈 쿼리로 요청하면 모든 학생의 히스토리가 반환된다
        mockMvc.perform(get("/student-schedule/history")
                        .param("query", query)
                        .param("day", day.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(studentScheduleService).getStudentScheduleHistory(query, day);
    }

    @Test
    @DisplayName("GET /student-schedule/history - 이름으로 여러 학생을 조회할 수 있다")
    void shouldGetHistoryByName() throws Exception {
        // Given: 이름 쿼리와 날짜가 있을 때
        String query = "허";
        LocalDate day = LocalDate.of(2024, 1, 15);

        HistoryStudentScheduleResponse response1 = HistoryStudentScheduleResponse.builder()
                .studentNumber(2115).name("허온")
                .eightAndNinePeriod(ScheduleType.SELF_STUDY).build();
        HistoryStudentScheduleResponse response2 = HistoryStudentScheduleResponse.builder()
                .studentNumber(2116).name("허준")
                .eightAndNinePeriod(ScheduleType.AWAY).build();

        given(studentScheduleService.getStudentScheduleHistory(query, day))
                .willReturn(List.of(response1, response2));

        // When & Then: 이름으로 요청하면 이름이 일치하는 학생들의 히스토리가 반환된다
        mockMvc.perform(get("/student-schedule/history")
                        .param("query", query)
                        .param("day", day.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("허온"))
                .andExpect(jsonPath("$[1].name").value("허준"));

        verify(studentScheduleService).getStudentScheduleHistory(query, day);
    }
}
