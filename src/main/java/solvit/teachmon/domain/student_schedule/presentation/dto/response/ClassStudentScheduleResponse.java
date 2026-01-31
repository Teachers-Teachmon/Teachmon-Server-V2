package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record ClassStudentScheduleResponse(
        @JsonProperty("class")
        Integer classNumber,

        List<StudentScheduleResponse> students
) {}
