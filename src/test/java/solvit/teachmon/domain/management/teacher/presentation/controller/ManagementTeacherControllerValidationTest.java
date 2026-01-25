package solvit.teachmon.domain.management.teacher.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.global.enums.WeekDay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("선생님 관리 컨트롤러 - 유효성 검사 테스트")
class ManagementTeacherControllerValidationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("선생님 정보 수정 시 role이 null이면 400 에러가 발생한다")
    void shouldReturn400WhenRoleIsNull() throws Exception {
        // Given
        TeacherUpdateRequest request = new TeacherUpdateRequest(null, "김선생");

        // When & Then
        mockMvc.perform(patch("/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 정보 수정 시 name이 null이면 400 에러가 발생한다")
    void shouldReturn400WhenNameIsNull() throws Exception {
        // Given
        TeacherUpdateRequest request = new TeacherUpdateRequest(Role.TEACHER, null);

        // When & Then
        mockMvc.perform(patch("/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 정보 수정 시 name이 빈 문자열이면 400 에러가 발생한다")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        // Given
        TeacherUpdateRequest request = new TeacherUpdateRequest(Role.TEACHER, "");

        // When & Then
        mockMvc.perform(patch("/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 정보 수정 시 name이 공백만 있으면 400 에러가 발생한다")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        TeacherUpdateRequest request = new TeacherUpdateRequest(Role.TEACHER, "   ");

        // When & Then
        mockMvc.perform(patch("/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 정보 수정 시 teacher_id가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenTeacherIdIsLessThan1() throws Exception {
        // Given
        TeacherUpdateRequest request = new TeacherUpdateRequest(Role.TEACHER, "김선생");

        // When & Then
        mockMvc.perform(patch("/teacher/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 삭제 시 teacher_id가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenDeleteTeacherIdIsLessThan1() throws Exception {
        // When & Then
        mockMvc.perform(delete("/teacher/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 금지날 조회 시 teacher_id가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenGetBanDayTeacherIdIsLessThan1() throws Exception {
        // When & Then
        mockMvc.perform(get("/teacher/0/ban"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 금지날 설정 시 teacher_id가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenSetBanDayTeacherIdIsLessThan1() throws Exception {
        // Given
        List<WeekDay> banDays = Arrays.asList(WeekDay.MON, WeekDay.TUE);

        // When & Then
        mockMvc.perform(post("/teacher/0/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(banDays)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("선생님 조회 시 query 파라미터가 있으면 200이 반환된다")
    void shouldReturn200WhenGetTeachersWithQuery() throws Exception {
        // When & Then
        mockMvc.perform(get("/teacher")
                        .param("query", "김"))
                .andExpect(status().isOk());
    }
}
