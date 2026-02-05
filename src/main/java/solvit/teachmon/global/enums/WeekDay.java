package solvit.teachmon.global.enums;

import java.time.LocalDate;

public enum WeekDay {
    MON,
    TUE,
    WED,
    THU;

    public static WeekDay fromLocalDate(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> MON;
            case TUESDAY -> TUE;
            case WEDNESDAY -> WED;
            case THURSDAY -> THU;
            default -> throw new IllegalArgumentException("서비스에서 지원하는 요일이 아닙니다.");
        };
    }
}
