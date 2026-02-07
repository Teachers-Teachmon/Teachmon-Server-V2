package solvit.teachmon.domain.student_schedule.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class AfterSchoolScheduleValueInvalidException extends TeachmonBusinessException {
    public AfterSchoolScheduleValueInvalidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    public AfterSchoolScheduleValueInvalidException(String message, HttpStatus status) {
        super(message, status);
    }
}
