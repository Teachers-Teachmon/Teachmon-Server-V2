package solvit.teachmon.domain.management.student.presentation.controller;

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
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("학생 관리 컨트롤러 - 유효성 검사 테스트")
class ManagementStudentControllerValidationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("학생 생성 시 grade가 null이면 400 에러가 발생한다")
    void shouldReturn400WhenGradeIsNull() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(null, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 grade가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenGradeIsLessThan1() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(0, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 grade가 3 초과이면 400 에러가 발생한다")
    void shouldReturn400WhenGradeIsGreaterThan3() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(4, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 classNumber가 null이면 400 에러가 발생한다")
    void shouldReturn400WhenClassNumberIsNull() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, null, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 classNumber가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenClassNumberIsLessThan1() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 0, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 number가 null이면 400 에러가 발생한다")
    void shouldReturn400WhenNumberIsNull() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, null, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 number가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenNumberIsLessThan1() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 0, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 name이 null이면 400 에러가 발생한다")
    void shouldReturn400WhenNameIsNull() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 1, null);

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 name이 빈 문자열이면 400 에러가 발생한다")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 1, "");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 name이 공백만 있으면 400 에러가 발생한다")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 1, "   ");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 수정 시 student_id가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenStudentIdIsLessThan1() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(patch("/student/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 삭제 시 student_id가 1 미만이면 400 에러가 발생한다")
    void shouldReturn400WhenDeleteStudentIdIsLessThan1() throws Exception {
        // When & Then
        mockMvc.perform(delete("/student/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("학생 생성 시 모든 필드가 유효하면 200이 반환된다")
    void shouldReturn200WhenCreateStudentWithValidRequest() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("학생 생성 시 grade가 경계값(1)이면 정상 처리된다")
    void shouldReturn200WhenGradeIs1() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(1, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("학생 생성 시 grade가 경계값(3)이면 정상 처리된다")
    void shouldReturn200WhenGradeIs3() throws Exception {
        // Given
        StudentRequest request = new StudentRequest(3, 1, 1, "홍길동");

        // When & Then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
