package solvit.teachmon.domain.after_school.exception;

public class BranchNullException extends TeachmonSpreadSheetException {
    
    public BranchNullException(long rowNum) {
        super(rowNum + "행의 분기 값이 비어있습니다. 1~4 사이의 값을 입력해주세요.");
    }
}
