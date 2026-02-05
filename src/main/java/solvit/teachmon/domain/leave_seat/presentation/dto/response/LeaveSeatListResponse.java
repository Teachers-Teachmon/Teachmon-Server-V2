package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.util.List;

public record LeaveSeatListResponse(
        @JsonProperty("leaveseat_id")
        Long leaveSeatId,

        SchoolPeriod period,

        String teacher,

        String place,

        Integer personnel,

        List<String> students
) {
}
