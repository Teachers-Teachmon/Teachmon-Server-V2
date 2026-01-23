package solvit.teachmon.domain.user.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class TeacherInvalidValueException extends TeachmonBusinessException {
    public TeacherInvalidValueException(String message, HttpStatus status) {
        super(message, status);
    }
}
