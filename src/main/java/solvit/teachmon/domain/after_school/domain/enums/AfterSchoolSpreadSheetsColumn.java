package solvit.teachmon.domain.after_school.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AfterSchoolSpreadSheetsColumn {
    YEAR(0, "연도"),
    BRANCH(1, "분기"),
    WEEKDAY(2, "요일"),
    GRADE(3, "학년"),
    PERIOD(4, "교시"),
    TEACHER(5, "담당교사"),
    PLACE(6, "장소이름"),
    NAME(7, "이름"),
    STUDENTS(8, "학생");

    private final int index;
    private final String headerName;
}
