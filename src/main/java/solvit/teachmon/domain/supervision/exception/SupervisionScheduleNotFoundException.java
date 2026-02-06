package solvit.teachmon.domain.supervision.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class SupervisionScheduleNotFoundException extends ResourceNotFoundException {
    public SupervisionScheduleNotFoundException() {
        super("요청한 감독 일정을 찾을 수 없습니다.");
    }
}