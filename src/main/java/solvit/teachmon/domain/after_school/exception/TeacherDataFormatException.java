package solvit.teachmon.domain.after_school.exception;

public class TeacherDataFormatException extends TeachmonSpreadSheetException {
    
    public TeacherDataFormatException(long rowNum, String teacherData) {
        super(rowNum + "행의 선생님 데이터 형식이 올바르지 않습니다. 입력값: '" + teacherData + "', 기대형식: '이름(이메일)'");
    }
}
