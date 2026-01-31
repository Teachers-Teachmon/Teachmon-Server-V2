package solvit.teachmon.domain.after_school.exception;

public class BranchRangeException extends TeachmonSpreadSheetException {
    
    public BranchRangeException(long rowNum, String actualValue) {
        super(rowNum + "행의 분기 값이 올바르지 않습니다. 입력값: '" + actualValue + "', 허용값: 1~4");
    }
}
