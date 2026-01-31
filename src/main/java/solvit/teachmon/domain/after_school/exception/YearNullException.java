package solvit.teachmon.domain.after_school.exception;

public class YearNullException extends TeachmonSpreadSheetException {
    
    public YearNullException(long rowNum) {
        super(rowNum + "행의 연도 값이 비어있습니다. 4자리 연도를 입력해주세요.");
    }
}
