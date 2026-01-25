package solvit.teachmon.global.enums;

/**
 * 요일을 나타내는 열거형 (월~목요일만 포함)
 * Enumeration representing weekdays (Monday through Thursday only)
 * 
 * <p>금요일이 제외된 이유: 학교 감독 및 자습 일정이 월~목요일에만 운영되기 때문입니다.
 * Friday is excluded because school supervision and self-study schedules operate only from Monday to Thursday.
 * 
 * <p>사용처:
 * Usage:
 * <ul>
 *   <li>자습 일정 관리 (Self-study schedule management)</li>
 *   <li>감독 일정 관리 (Supervision schedule management)</li>
 * </ul>
 */
public enum WeekDay {
    /** 월요일 (Monday) */
    MON,
    /** 화요일 (Tuesday) */
    TUE,
    /** 수요일 (Wednesday) */
    WED,
    /** 목요일 (Thursday) */
    THU
}
