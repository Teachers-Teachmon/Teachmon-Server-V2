package solvit.teachmon.domain.supervision.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;

import java.time.LocalDate;

@Builder
public record SupervisionExchangeResponseDto(
        @JsonProperty("id")
        Long id,

        @JsonProperty("requestor")
        SupervisionInfo requestor,

        @JsonProperty("responser")
        SupervisionInfo responser,

        @JsonProperty("status")
        SupervisionExchangeType status,

        @JsonProperty("reason")
        String reason
) {
    @Builder
    public record SupervisionInfo(
            @JsonProperty("teacher")
            TeacherInfo teacher,

            @JsonProperty("day")
            LocalDate day,

            @JsonProperty("type")
            String type
    ) {
        @Builder
        public record TeacherInfo(
                @JsonProperty("id")
                Long id,

                @JsonProperty("name")
                String name
        ) {
        }
    }
}