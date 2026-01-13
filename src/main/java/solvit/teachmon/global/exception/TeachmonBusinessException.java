package solvit.teachmon.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TeachmonBusinessException extends TeachmonException {

    public TeachmonBusinessException(String message, HttpStatus status) {
        super(message, status);
    }
}
