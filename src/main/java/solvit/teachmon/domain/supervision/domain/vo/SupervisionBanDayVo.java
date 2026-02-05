package solvit.teachmon.domain.supervision.domain.vo;

import solvit.teachmon.global.enums.WeekDay;

/**
 * 교사 금지요일 정보 값 객체
 */
public record SupervisionBanDayVo(
        Long teacherId,
        WeekDay weekDay
) {
}