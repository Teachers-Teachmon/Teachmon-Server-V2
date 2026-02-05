package solvit.teachmon.domain.team.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class InvalidTeamParticipationInfoException extends TeachmonBusinessException {
    public InvalidTeamParticipationInfoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
