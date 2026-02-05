package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record FloorStateResponse(
        @JsonProperty("floor")
        Integer floor,

        @JsonProperty("count")
        Long count
) {
}
