package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

public record FixedLeaveSeatDetailResponse(
        @JsonProperty("week_day")
        WeekDay weekDay,

        SchoolPeriod period,

        PlaceInfoResponse place,

        String cause,

        List<StudentDetailInfoResponse> students
) {}
