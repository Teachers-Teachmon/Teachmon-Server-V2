package solvit.teachmon.domain.after_school.exception;

public class PeriodInvalidException extends TeachmonSpreadSheetException {
    
    public PeriodInvalidException(long rowNum, String actualValue) {
        super(rowNum + "행의 교시 값이 올바르지 않습니다. 입력값: '" + actualValue + "', 허용값: '8~9교시', '10~11교시'");
    }
}
