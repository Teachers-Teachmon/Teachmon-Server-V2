package solvit.teachmon.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TeachmonSystemError extends TeachmonException {

    public TeachmonSystemError(String message, HttpStatus status) {
        super(message, status);
    }
}
