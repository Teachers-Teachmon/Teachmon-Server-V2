package solvit.teachmon.domain.leave_seat.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

public record LeaveSeatUpdateRequest(
        @NotNull(message = "이석 수정에서 day(날짜)는 필수입니다.")
        LocalDate day,

        @NotNull(message = "이석 수정에서 period(교시)는 필수입니다.")
        SchoolPeriod period,

        @NotNull(message = "이석 수정에서 place(장소 id)는 필수입니다.")
        Long place,

        @NotNull(message = "이석 수정에서 cause(사유)는 필수입니다.")
        String cause,

        @NotEmpty(message = "이석 수정에서 students(학생 id)는 최소 1명 이상이어야 합니다.")
        List<Long> students
) {
}
