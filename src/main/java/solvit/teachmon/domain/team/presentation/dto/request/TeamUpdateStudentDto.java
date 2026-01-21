package solvit.teachmon.domain.team.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record TeamUpdateStudentDto(
        @NotNull(message = "학생 id(아이디)는 필수입니다.")
        Long id,
        
        @JsonProperty("student_number")
        @NotNull(message = "학생 student_number(학번)는 필수입니다.")
        Integer studentNumber,
        
        @NotNull(message = "학생 name(이름)은 필수입니다.")
        String name
) {}