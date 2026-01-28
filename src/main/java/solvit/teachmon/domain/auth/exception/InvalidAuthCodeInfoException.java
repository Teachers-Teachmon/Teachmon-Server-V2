package solvit.teachmon.domain.auth.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidAuthCodeInfoException extends TeachmonBusinessException {
    public InvalidAuthCodeInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
