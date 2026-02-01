package solvit.teachmon.domain.supervision.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

/**
 * 감독 배정 가능한 교사 수 부족 예외
 */
public class InsufficientTeachersException extends ResourceNotFoundException {
    public InsufficientTeachersException(String message) {
        super(message);
    }
}