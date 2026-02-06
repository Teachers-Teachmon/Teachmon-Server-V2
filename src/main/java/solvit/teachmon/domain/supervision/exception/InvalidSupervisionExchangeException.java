package solvit.teachmon.domain.supervision.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonException;

public class InvalidSupervisionExchangeException extends TeachmonException {
    public InvalidSupervisionExchangeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}