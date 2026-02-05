package solvit.teachmon.domain.after_school.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

public record AfterSchoolReinforcementRequestDto(
        @NotNull(message = "날짜는 필수입니다.")
        LocalDate day,

        @JsonProperty("afterschool_id")
        @NotNull(message = "방과후 ID는 필수입니다.")
        Long afterschoolId,

        @JsonProperty("change_period")
        @NotNull(message = "변경 교시는 필수입니다.")
        SchoolPeriod changePeriod,

        @JsonProperty("change_place_id")
        @NotNull(message = "변경 장소 ID는 필수입니다.")
        Long changePlaceId
) {
}