package solvit.teachmon.domain.team.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidTeamInfoException extends TeachmonBusinessException {
    public InvalidTeamInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
