package solvit.teachmon.domain.student_schedule.domain.exception;

public class IllegalStudentStateChangeException extends IllegalStateException {
    public IllegalStudentStateChangeException() {
        super("허용되지 않는 학생 상태 변경입니다");
    }
}