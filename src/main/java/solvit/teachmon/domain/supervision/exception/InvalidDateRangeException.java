package solvit.teachmon.domain.supervision.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

/**
 * 잘못된 날짜 범위 예외
 */
public class InvalidDateRangeException extends TeachmonBusinessException {
    public InvalidDateRangeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}