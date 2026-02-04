package solvit.teachmon.domain.after_school.presentation.dto.request;

import solvit.teachmon.global.enums.WeekDay;

public record AfterSchoolSearchRequestDto(
        Integer grade,
        Integer branch,
        WeekDay weekDay,
        Integer startPeriod,
        Integer endPeriod
) {
}
