package solvit.teachmon.domain.branch.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidBranchInfoException extends TeachmonBusinessException {
    public InvalidBranchInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}