package solvit.teachmon.domain.leave_seat.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class LeaveSeatNotFoundException extends ResourceNotFoundException {
    public LeaveSeatNotFoundException() {
        super("요청한 이석을 찾을 수 없습니다.");
    }
}
