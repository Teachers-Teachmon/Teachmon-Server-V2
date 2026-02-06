package solvit.teachmon.domain.supervision.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonException;

public class InvalidSupervisionScheduleException extends TeachmonException {
    public InvalidSupervisionScheduleException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}