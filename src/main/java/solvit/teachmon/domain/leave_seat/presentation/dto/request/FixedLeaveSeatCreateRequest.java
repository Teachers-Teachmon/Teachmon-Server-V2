package solvit.teachmon.domain.leave_seat.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

public record FixedLeaveSeatCreateRequest(
        @JsonProperty("week_day")
        @NotNull(message = "고정 이석 작성에서 week_day(요일)는 필수입니다.")
        WeekDay weekDay,

        @NotNull(message = "고정 이석 작성에서 period(교시)는 필수입니다.")
        SchoolPeriod period,

        @JsonProperty("place_id")
        @NotNull(message = "고정 이석 작성에서 place_id(장소 id)는 필수입니다.")
        Long placeId,

        @NotNull(message = "고정 이석 작성에서 cause(사유)는 필수입니다.")
        String cause,

        @NotEmpty(message = "학생 이석 작성에서 students(학생 id)는 최소 1명 이상이어야 합니다.")
        List<Long> students
) {}
