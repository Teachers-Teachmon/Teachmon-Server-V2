package solvit.teachmon.domain.user.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidTeacherInfoException extends TeachmonBusinessException {
    public InvalidTeacherInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
