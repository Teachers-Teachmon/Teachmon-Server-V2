package solvit.teachmon.domain.auth.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidTokenInfoException extends TeachmonBusinessException {
    public InvalidTokenInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
