package solvit.teachmon.domain.after_school.exception;

public class StudentDataFormatException extends TeachmonSpreadSheetException {
    
    public StudentDataFormatException(long rowNum, String studentData) {
        super(rowNum + "행의 학생 데이터 형식이 올바르지 않습니다. 입력값: '" + studentData + "', 기대형식: '학번 이름 학번 이름...'");
    }
}
