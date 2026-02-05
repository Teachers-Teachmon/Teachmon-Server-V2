package solvit.teachmon.domain.after_school.exception;

public class StudentNotExistException extends TeachmonSpreadSheetException {
    
    public StudentNotExistException(long rowNum, Long studentNumber, String studentName) {
        super(rowNum + "행의 학생 정보가 시스템에 등록되지 않았습니다. 학번: " + studentNumber + ", 이름: '" + studentName + "'");
    }
}
