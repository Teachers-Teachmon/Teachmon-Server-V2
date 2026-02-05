package solvit.teachmon.domain.management.student.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentRequest(
        @NotNull(message = "학생 생성에서 grade(학년)는 필수입니다.")
        @Min(value = 1, message = "학생 생성에서 grade(학년)는 1 이상이어야 합니다.")
        @Max(value = 3, message = "학생 생성에서 grade(학년)는 3 이하이어야 합니다.")
        Integer grade,

        @NotNull(message = "학생 생성에서 class(반)는 필수입니다.")
        @Min(value = 1, message = "학생 생성에서 class(반)는 1 이상이어야 합니다.")
        @JsonProperty("class")
        Integer classNumber,

        @NotNull(message = "학생 생성에서 number(번호)는 필수입니다.")
        @Min(value = 1, message = "학생 생성에서 number(번호)는 1 이상이어야 합니다.")
        Integer number,

        @NotBlank(message = "학생 생성에서 name(이름)은 필수입니다.")
        String name
) {}
