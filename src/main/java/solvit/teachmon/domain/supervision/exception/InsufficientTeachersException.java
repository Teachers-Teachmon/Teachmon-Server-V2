package solvit.teachmon.domain.supervision.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

/**
 * 감독 배정 가능한 교사 수 부족 예외
 */
public class InsufficientTeachersException extends TeachmonBusinessException {
    public InsufficientTeachersException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}