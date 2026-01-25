package solvit.teachmon.domain.management.teacher.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidSupervisionBanDayInfoException extends TeachmonBusinessException {
    public InvalidSupervisionBanDayInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
