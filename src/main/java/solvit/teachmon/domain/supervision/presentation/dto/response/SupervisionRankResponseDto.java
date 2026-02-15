package solvit.teachmon.domain.supervision.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SupervisionRankResponseDto(
        Integer rank,

        String name,

        @JsonProperty("self_study_supervision_count")
        Integer selfStudySupervisionCount,

        @JsonProperty("leave_seat_supervision_count")
        Integer leaveSeatSupervisionCount,

        @JsonProperty("seventh_period_supervision_count")
        Integer seventhPeriodSupervisionCount,

        @JsonProperty("total_supervision_count")
        Integer totalSupervisionCount
) {}