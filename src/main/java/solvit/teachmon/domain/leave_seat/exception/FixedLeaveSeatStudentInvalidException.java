package solvit.teachmon.domain.leave_seat.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class FixedLeaveSeatStudentInvalidException extends TeachmonBusinessException {
    public FixedLeaveSeatStudentInvalidException(String message, HttpStatus status) {
        super(message, status);
    }
}
