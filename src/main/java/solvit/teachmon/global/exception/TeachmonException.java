package solvit.teachmon.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TeachmonException extends RuntimeException {
    private final HttpStatus status;

    public TeachmonException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}