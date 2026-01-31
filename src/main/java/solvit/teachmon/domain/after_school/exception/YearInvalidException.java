package solvit.teachmon.domain.after_school.exception;

public class YearInvalidException extends TeachmonSpreadSheetException {
    
    public YearInvalidException(long rowNum, String actualValue) {
        super(rowNum + "행의 연도 값이 올바르지 않습니다. 입력값: '" + actualValue + "', 기대형식: 4자리 연도 (예: 2024)");
    }
}
