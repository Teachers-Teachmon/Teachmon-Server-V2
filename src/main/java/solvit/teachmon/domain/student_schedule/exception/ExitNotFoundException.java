package solvit.teachmon.domain.student_schedule.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class ExitNotFoundException extends ResourceNotFoundException {
    public ExitNotFoundException() {
        super("이탈 기록을 찾을 수 없습니다.");
    }
}
