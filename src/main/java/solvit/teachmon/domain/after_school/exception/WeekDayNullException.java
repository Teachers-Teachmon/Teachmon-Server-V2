package solvit.teachmon.domain.after_school.exception;

public class WeekDayNullException extends TeachmonSpreadSheetException {
    
    public WeekDayNullException(long rowNum) {
        super(rowNum + "행의 요일 값이 비어있습니다.");
    }
}
