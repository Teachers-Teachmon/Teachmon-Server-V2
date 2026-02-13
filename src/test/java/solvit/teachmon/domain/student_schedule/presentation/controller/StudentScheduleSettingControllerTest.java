package solvit.teachmon.domain.student_schedule.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleSettingService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 설정 컨트롤러 테스트")
class StudentScheduleSettingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentScheduleSettingService studentScheduleSettingService;

    @InjectMocks
    private StudentScheduleSettingController studentScheduleSettingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentScheduleSettingController)
                .defaultResponseCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .build();
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - 주간 학생 스케줄을 설정할 수 있다")
    void shouldSettingWeeklyStudentSchedule() throws Exception {
        // Given: 기준 날짜가 주어졌을 때
        LocalDate baseDay = LocalDate.of(2024, 1, 15); // 월요일

        // When: POST 요청을 보내면
        mockMvc.perform(post("/student-schedule/setting/weekly")
                        .param("base_day", baseDay.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        // Then: 서비스의 createNewStudentSchedule과 settingAllTypeSchedule가 순서대로 호출되어야 한다
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        verify(studentScheduleSettingService, times(1)).createNewStudentSchedule(captor.capture());
        verify(studentScheduleSettingService, times(1)).settingAllTypeSchedule(captor.capture());

        // 두 메서드 모두 전달받은 baseDay를 그대로 받아야 한다
        assertThat(captor.getAllValues()).allMatch(date -> date.equals(baseDay));
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - 어떤 요일이든 그대로 전달되어 처리된다")
    void shouldPassDateAsIs() throws Exception {
        // Given: 수요일이 주어졌을 때
        LocalDate wednesday = LocalDate.of(2024, 1, 17); // 수요일

        // When: POST 요청을 보내면
        mockMvc.perform(post("/student-schedule/setting/weekly")
                        .param("base_day", wednesday.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        // Then: 전달받은 날짜 그대로 서비스 메서드가 호출되어야 한다
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        verify(studentScheduleSettingService, times(1)).createNewStudentSchedule(captor.capture());
        verify(studentScheduleSettingService, times(1)).settingAllTypeSchedule(captor.capture());

        assertThat(captor.getAllValues()).allMatch(date -> date.equals(wednesday));
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - 일요일도 그대로 전달되어 처리된다")
    void shouldPassSundayAsIs() throws Exception {
        // Given: 일요일이 주어졌을 때
        LocalDate sunday = LocalDate.of(2024, 1, 21); // 일요일

        // When: POST 요청을 보내면
        mockMvc.perform(post("/student-schedule/setting/weekly")
                        .param("base_day", sunday.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        // Then: 전달받은 날짜 그대로 서비스 메서드가 호출되어야 한다
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        verify(studentScheduleSettingService, times(1)).createNewStudentSchedule(captor.capture());
        verify(studentScheduleSettingService, times(1)).settingAllTypeSchedule(captor.capture());

        assertThat(captor.getAllValues()).allMatch(date -> date.equals(sunday));
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - base_day 파라미터가 없으면 400 에러를 반환한다")
    void shouldReturn400WhenBaseDayIsMissing() throws Exception {
        // When & Then: base_day 파라미터 없이 요청하면 400 에러가 발생한다
        mockMvc.perform(post("/student-schedule/setting/weekly"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 서비스 메서드가 호출되지 않아야 한다
        verify(studentScheduleSettingService, never()).createNewStudentSchedule(any());
        verify(studentScheduleSettingService, never()).settingAllTypeSchedule(any());
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - 잘못된 날짜 형식이면 400 에러를 반환한다")
    void shouldReturn400WhenBaseDayIsInvalid() throws Exception {
        // When & Then: 잘못된 날짜 형식으로 요청하면 400 에러가 발생한다
        mockMvc.perform(post("/student-schedule/setting/weekly")
                        .param("base_day", "invalid-date"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 서비스 메서드가 호출되지 않아야 한다
        verify(studentScheduleSettingService, never()).createNewStudentSchedule(any());
        verify(studentScheduleSettingService, never()).settingAllTypeSchedule(any());
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - createNewStudentSchedule이 먼저 호출되고 settingAllTypeSchedule이 나중에 호출되어야 한다")
    void shouldCallMethodsInOrder() throws Exception {
        // Given: 기준 날짜가 주어졌을 때
        LocalDate baseDay = LocalDate.of(2024, 1, 15);

        // When: POST 요청을 보내면
        mockMvc.perform(post("/student-schedule/setting/weekly")
                        .param("base_day", baseDay.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        // Then: 메서드 호출 순서를 검증한다
        var inOrder = inOrder(studentScheduleSettingService);
        inOrder.verify(studentScheduleSettingService).createNewStudentSchedule(any(LocalDate.class));
        inOrder.verify(studentScheduleSettingService).settingAllTypeSchedule(any(LocalDate.class));
    }

    @Test
    @DisplayName("POST /student-schedule/setting/weekly - 다양한 연도에 대해서도 올바르게 동작한다")
    void shouldWorkWithDifferentYears() throws Exception {
        // Given: 2025년 날짜가 주어졌을 때
        LocalDate baseDay = LocalDate.of(2025, 3, 19); // 2025년 3월 19일 (수요일)

        // When: POST 요청을 보내면
        mockMvc.perform(post("/student-schedule/setting/weekly")
                        .param("base_day", baseDay.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        // Then: 전달받은 날짜 그대로 서비스 메서드가 호출되어야 한다
        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        verify(studentScheduleSettingService).createNewStudentSchedule(captor.capture());
        verify(studentScheduleSettingService).settingAllTypeSchedule(any(LocalDate.class));

        assertThat(captor.getValue()).isEqualTo(baseDay);
    }
}
