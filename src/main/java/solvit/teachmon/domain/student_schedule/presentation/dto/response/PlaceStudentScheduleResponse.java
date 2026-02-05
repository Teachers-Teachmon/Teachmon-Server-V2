package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record PlaceStudentScheduleResponse(
        @JsonProperty("place_id")
        Long placeId,

        @JsonProperty("place_name")
        String placeName,

        @JsonProperty("students")
        List<StudentScheduleResponse> students
) {}
