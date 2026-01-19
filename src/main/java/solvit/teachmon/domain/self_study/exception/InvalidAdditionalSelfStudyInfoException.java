package solvit.teachmon.domain.self_study.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidAdditionalSelfStudyInfoException extends TeachmonBusinessException {
    public InvalidAdditionalSelfStudyInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
