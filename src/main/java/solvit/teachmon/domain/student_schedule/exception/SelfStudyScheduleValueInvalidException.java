package solvit.teachmon.domain.student_schedule.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class SelfStudyScheduleValueInvalidException extends TeachmonBusinessException {
    public SelfStudyScheduleValueInvalidException(String message, HttpStatus status) {
        super(message, status);
    }
}
