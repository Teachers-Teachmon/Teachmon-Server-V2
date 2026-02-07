package solvit.teachmon.domain.student_schedule.domain.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class NoAvailablePlaceException extends TeachmonBusinessException
{
    public NoAvailablePlaceException() {
        super("배정 가능한 장소가 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
