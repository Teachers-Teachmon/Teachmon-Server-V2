package solvit.teachmon.domain.self_study.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

public record AdditionalSelfStudySetRequest(
        @NotNull(message = "추가 자습설정에서 day(날짜)는 필수 입니다")
        LocalDate day,

        @Min(value = 1, message = "추가 자습설정에서 grade(학년)는 1 이상 이어야 합니다")
        Integer grade,

        @NotNull(message = "추가 자습설정에서 periods(교시)는 [] 형식 입니다")
        List<SchoolPeriod> periods
) {}
