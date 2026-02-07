package solvit.teachmon.domain.student_schedule.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class StudentScheduleValueInvalidException extends TeachmonBusinessException {
    public StudentScheduleValueInvalidException(String message, HttpStatus status) {
      super(message, status);
    }
}
