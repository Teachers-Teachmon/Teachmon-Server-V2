package solvit.teachmon.domain.leave_seat.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class FixedLeaveSeatNotFoundException extends ResourceNotFoundException {
    public FixedLeaveSeatNotFoundException() {
        super("요청한 고정 이석을 찾을 수 없습니다.");
    }
}
