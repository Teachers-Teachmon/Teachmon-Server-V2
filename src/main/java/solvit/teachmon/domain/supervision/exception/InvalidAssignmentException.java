package solvit.teachmon.domain.supervision.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

/**
 * 잘못된 감독 배정 예외
 */
public class InvalidAssignmentException extends TeachmonBusinessException {
    public InvalidAssignmentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}