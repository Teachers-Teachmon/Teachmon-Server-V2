package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

@Builder
public record PlaceStateResponse(
        @JsonProperty("place_id")
        Long placeId,

        @JsonProperty("place_name")
        String placeName,

        @JsonProperty("state")
        ScheduleType state
) {}
