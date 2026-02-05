package solvit.teachmon.domain.after_school.exception;

public class TeacherNotExistException extends TeachmonSpreadSheetException {
    
    public TeacherNotExistException(long rowNum, String teacherName) {
        super(rowNum + "행의 담당교사 '" + teacherName + "'가 시스템에 등록되지 않았습니다.");
    }
}
