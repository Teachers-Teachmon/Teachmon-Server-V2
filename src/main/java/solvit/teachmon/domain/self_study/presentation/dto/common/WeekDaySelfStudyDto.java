package solvit.teachmon.domain.self_study.presentation.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

public record WeekDaySelfStudyDto(
        @JsonProperty("week_day")
        WeekDay weekDay,

        @NotNull(message = "자습설정에서 periods(교시)는 [] 형식 입니다")
        List<SchoolPeriod> periods
) {}
