package solvit.teachmon.domain.after_school.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record AfterSchoolBusinessTripRequestDto(
        @NotNull(message = "날짜는 필수입니다.")
        LocalDate day,

        @JsonProperty("afterschool_id")
        @NotNull(message = "방과후 ID는 필수입니다.")
        Long afterschoolId
) {
}