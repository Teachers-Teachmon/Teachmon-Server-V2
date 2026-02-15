package solvit.teachmon.domain.after_school.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AfterSchoolByTeacherResponseDto(
        Long id,
        @JsonProperty("week_day")
        String weekDay,
        String period,
        String name,
        PlaceInfo place,
        @JsonProperty("reinforcement_count")
        Integer reinforcementCount
) {
    public record PlaceInfo(
            Long id,
            String name
    ) {
    }
}