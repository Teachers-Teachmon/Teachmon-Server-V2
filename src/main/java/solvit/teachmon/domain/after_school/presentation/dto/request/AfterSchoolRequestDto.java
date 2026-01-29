package solvit.teachmon.domain.after_school.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AfterSchoolRequestDto(
        @NotNull(message = "방과후 생성 요청에서 grade(학년)는 필수입니다.")
        @Min(value = 1, message = "학년은 1부터 3까지만 가능합니다.")
        @Max(value = 3, message = "학년은 1부터 3까지만 가능합니다.")
        Integer grade,
        
        @JsonProperty("week_day")
        @NotNull(message = "방과후 생성 요청에서 week_day(요일)는 필수입니다.")
        String weekDay,
        
        @NotNull(message = "방과후 생성 요청에서 period(교시)는 필수입니다.")
        String period,
        
        @JsonProperty("teacher_id")
        @NotNull(message = "방과후 생성 요청에서 teacher_id(선생님 아이디)는 필수입니다.")
        Long teacherId,
        
        @JsonProperty("place_id")
        @NotNull(message = "방과후 생성 요청에서 place_id(장소 아이디)는 필수입니다.")
        Long placeId,
        
        @NotNull(message = "방과후 생성 요청에서 name(방과후 이름)은 필수입니다.")
        String name,
        
        @JsonProperty("students_id")
        @NotNull(message = "방과후 생성 요청에서 students_id(학생 아이디 목록)는 필수입니다.")
        List<Long> studentsId
) {
}