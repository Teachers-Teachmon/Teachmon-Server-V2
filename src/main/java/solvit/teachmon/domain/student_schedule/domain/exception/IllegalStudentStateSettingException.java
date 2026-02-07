package solvit.teachmon.domain.student_schedule.domain.exception;

public class IllegalStudentStateSettingException extends IllegalStateException {
    public IllegalStudentStateSettingException() {
        super("허용되지 않는 학생 스케줄 세팅입니다.");
    }
}
