package solvit.teachmon.domain.after_school.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AfterSchoolRequestDto(
        @JsonProperty("after_school_id")
        @NotNull(message = "방과후 수정 요청에서 after_school_id는 필수입니다.")
        Long afterSchoolId,

        @Min(value = 1, message = "학년은 1부터 3까지만 가능합니다.")
        @Max(value = 3, message = "학년은 1부터 3까지만 가능합니다.")
        Integer grade,

        @JsonProperty("week_day")
        String weekDay,

        String period,

        @JsonProperty("teacher_id")
        Long teacherId,

        @JsonProperty("place_id")
        Long placeId,

        String name,

        @JsonProperty("students_id")
        List<Long> studentsId
) {
}
