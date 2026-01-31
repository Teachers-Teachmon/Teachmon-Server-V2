package solvit.teachmon.domain.place.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class PlaceNotFoundException extends TeachmonBusinessException {
    public PlaceNotFoundException() {
        super("해당 장소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
