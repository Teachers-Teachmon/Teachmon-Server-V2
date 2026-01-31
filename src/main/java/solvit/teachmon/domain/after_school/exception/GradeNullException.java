package solvit.teachmon.domain.after_school.exception;

public class GradeNullException extends TeachmonSpreadSheetException {
    
    public GradeNullException(long rowNum) {
        super(rowNum + "행의 학년 값이 비어있습니다. 1~3 사이의 값을 입력해주세요.");
    }
}
