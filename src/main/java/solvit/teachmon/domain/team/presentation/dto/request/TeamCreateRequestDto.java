package solvit.teachmon.domain.team.presentation.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record TeamCreateRequestDto(
        @NotNull(message = "팀 생성 요청에서 name(이름)은 필수입니다.")
        String name,
        @JsonProperty("students_id")
        @NotNull(message = "팀 생성 요청에서 students_id(학생들 아이디)는 필수입니다.")
        List<Long> students
) {
}
