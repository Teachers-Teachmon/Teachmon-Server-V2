package solvit.teachmon.domain.supervision.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class SupervisionExchangeNotFoundException extends ResourceNotFoundException {
    public SupervisionExchangeNotFoundException() {
        super("요청한 감독 교체 요청을 찾을 수 없습니다.");
    }
}