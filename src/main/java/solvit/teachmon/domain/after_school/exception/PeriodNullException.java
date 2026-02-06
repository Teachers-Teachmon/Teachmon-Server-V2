package solvit.teachmon.domain.after_school.exception;

public class PeriodNullException extends TeachmonSpreadSheetException {
    
    public PeriodNullException(long rowNum) {
        super(rowNum + "행의 교시 값이 비어있습니다.");
    }
}
