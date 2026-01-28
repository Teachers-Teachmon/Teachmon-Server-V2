package solvit.teachmon.domain.leave_seat.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class LeaveSeatStudentInvalidException extends TeachmonBusinessException {
    public LeaveSeatStudentInvalidException(String message, HttpStatus status) {
        super(message, status);
    }
}
