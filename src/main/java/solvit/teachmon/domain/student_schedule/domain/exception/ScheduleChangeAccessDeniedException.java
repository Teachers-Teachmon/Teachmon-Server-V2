package solvit.teachmon.domain.student_schedule.domain.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class ScheduleChangeAccessDeniedException extends TeachmonBusinessException {
    public ScheduleChangeAccessDeniedException() {
        super("학생 스케줄을 변경할 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
}
