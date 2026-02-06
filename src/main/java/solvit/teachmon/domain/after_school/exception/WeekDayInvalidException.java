package solvit.teachmon.domain.after_school.exception;

public class WeekDayInvalidException extends TeachmonSpreadSheetException {
    
    public WeekDayInvalidException(long rowNum, String actualValue) {
        super(rowNum + "행의 요일 값이 올바르지 않습니다. 입력값: '" + actualValue + "', 허용값: 월요일~금요일");
    }
}
