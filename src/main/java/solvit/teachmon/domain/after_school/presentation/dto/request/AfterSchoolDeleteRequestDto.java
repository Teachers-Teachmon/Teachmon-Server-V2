package solvit.teachmon.domain.after_school.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AfterSchoolDeleteRequestDto(
        @JsonProperty("after_school_id")
        @NotNull(message = "방과후 삭제 요청에서 after_school_id는 필수입니다.")
        Long afterSchoolId
) {
}
