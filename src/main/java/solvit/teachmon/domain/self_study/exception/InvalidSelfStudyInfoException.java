package solvit.teachmon.domain.self_study.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidSelfStudyInfoException extends TeachmonBusinessException {
    public InvalidSelfStudyInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
