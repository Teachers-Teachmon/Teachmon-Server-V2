package solvit.teachmon.domain.place.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class PlaceNotFoundException extends ResourceNotFoundException {
    public PlaceNotFoundException() {
        super("장소를 찾을 수 없습니다.");
    }

    public PlaceNotFoundException(String message) {
        super(message);
    }
}
