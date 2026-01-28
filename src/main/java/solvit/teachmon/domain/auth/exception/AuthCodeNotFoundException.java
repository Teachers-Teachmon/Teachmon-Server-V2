package solvit.teachmon.domain.auth.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class AuthCodeNotFoundException extends ResourceNotFoundException {
    public AuthCodeNotFoundException() {
        super("Auth Code가 존재하지 않습니다.");
    }
}
