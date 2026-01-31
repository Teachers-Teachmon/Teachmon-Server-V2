package solvit.teachmon.domain.after_school.exception;

public class TeacherNullException extends TeachmonSpreadSheetException {
    
    public TeacherNullException(long rowNum) {
        super(rowNum + "행의 담당교사 값이 비어있습니다.");
    }
}
