package solvit.teachmon.domain.oauth2.exception;

import org.springframework.http.HttpStatus;
import solvit.teachmon.global.exception.TeachmonBusinessException;

public class UnsupportedOAuth2ProviderException extends TeachmonBusinessException {
    public UnsupportedOAuth2ProviderException(String provider) {
        super(provider + "기관의 OAuth2는 지원하지 않습니다.", HttpStatus.BAD_REQUEST);
    }
}
