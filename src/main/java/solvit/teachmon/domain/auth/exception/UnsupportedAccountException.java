package solvit.teachmon.domain.oauth2.exception;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedAccountException extends AuthenticationException {
     public UnsupportedAccountException() {
        super("해당 계정은 이 서비스를 이용할 수 없습니다.");
    }
}
