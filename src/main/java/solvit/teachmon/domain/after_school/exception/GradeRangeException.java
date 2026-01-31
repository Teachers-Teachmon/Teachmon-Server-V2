package solvit.teachmon.domain.after_school.exception;

public class GradeRangeException extends TeachmonSpreadSheetException {
    
    public GradeRangeException(long rowNum, String actualValue) {
        super(rowNum + "행의 학년 값이 올바르지 않습니다. 입력값: '" + actualValue + "', 허용값: 1~3");
    }
}
