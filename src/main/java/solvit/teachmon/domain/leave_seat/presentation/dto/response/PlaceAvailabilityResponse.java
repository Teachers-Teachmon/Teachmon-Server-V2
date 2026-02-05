package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PlaceAvailabilityResponse(
        @JsonProperty("is_empty")
        Boolean isEmpty
) {
}
