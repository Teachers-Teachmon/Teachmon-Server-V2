package solvit.teachmon.domain.place;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class PlaceNotFoundException extends ResourceNotFoundException {
    public PlaceNotFoundException() {
        super("요청한 장소를 찾을 수 없습니다.");
    }
}
