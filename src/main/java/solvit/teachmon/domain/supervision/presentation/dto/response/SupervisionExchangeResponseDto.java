package solvit.teachmon.domain.supervision.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;

import java.time.LocalDate;

@Builder
public record SupervisionExchangeResponseDto(
        Long id,

        SupervisionInfo requestor,

        SupervisionInfo responser,

        SupervisionExchangeType status,

        String reason
) {
    @Builder
    public record SupervisionInfo(
            TeacherInfo teacher,

            LocalDate day,

            String type
    ) {
        @Builder
        public record TeacherInfo(
                Long id,

                String name
        ) {
        }
    }
}