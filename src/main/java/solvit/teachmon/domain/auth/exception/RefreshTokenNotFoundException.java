package solvit.teachmon.domain.auth.exception;

import solvit.teachmon.global.entity.exception.ResourceNotFoundException;

public class RefreshTokenNotFoundException extends ResourceNotFoundException {
    public RefreshTokenNotFoundException() {
        super("Refresh Token이 존재하지 않습니다.");
    }
}
