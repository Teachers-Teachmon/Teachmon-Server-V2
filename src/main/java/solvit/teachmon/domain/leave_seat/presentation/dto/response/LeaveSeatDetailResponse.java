package solvit.teachmon.domain.leave_seat.presentation.dto.response;

import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

public record LeaveSeatDetailResponse(
        LocalDate day,

        String teacher,

        SchoolPeriod period,

        PlaceInfoResponse place,

        String cause,

        List<StudentInfoResponse> students
) {}
