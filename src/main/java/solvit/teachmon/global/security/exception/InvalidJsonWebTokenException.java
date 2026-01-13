package solvit.teachmon.global.security.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidJsonWebTokenException extends TeachmonBusinessException {
    public InvalidJsonWebTokenException() {
        super("올바르지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
    }
}
