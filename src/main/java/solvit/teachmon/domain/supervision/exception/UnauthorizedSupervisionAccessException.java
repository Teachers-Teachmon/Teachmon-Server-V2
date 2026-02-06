package solvit.teachmon.domain.supervision.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

/**
 * 감독 일정 접근 권한 없음 예외
 */
public class UnauthorizedSupervisionAccessException extends TeachmonBusinessException {
    public UnauthorizedSupervisionAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}