package solvit.teachmon.domain.after_school.exception;

public class StudentDataNullException extends TeachmonSpreadSheetException {
    
    public StudentDataNullException(long rowNum) {
        super(rowNum + "행의 학생 데이터가 비어있습니다.");
    }
}
