package solvit.teachmon.domain.after_school.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidAfterSchoolBusinessTripException extends TeachmonBusinessException {
    public InvalidAfterSchoolBusinessTripException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}