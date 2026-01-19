package solvit.teachmon.domain.auth.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;
import solvit.teachmon.domain.auth.domain.repository.AuthCodeRepository;
import solvit.teachmon.domain.auth.exception.AuthCodeNotFoundException;
import solvit.teachmon.global.properties.AuthCodeProperties;

@Service
@RequiredArgsConstructor
public class AuthCodeService {
    private final AuthCodeRepository authCodeRepository;
    private final AuthCodeProperties authCodeProperties;

    public void create(String authCode, String accessToken) {
        AuthCodeEntity authCodeEntity = AuthCodeEntity.builder()
                .authCode(authCode)
                .accessToken(accessToken)
                .timeToLive(authCodeProperties.getExpiration())
                .build();
        authCodeRepository.save(authCodeEntity);
    }

    public void delete(String authCode) {
        authCodeRepository.deleteById(authCode);
    }

    public String getAccessTokenByAuthCode(String authCode) {
        AuthCodeEntity authCodeEntity = authCodeRepository.findById(authCode).orElseThrow(AuthCodeNotFoundException::new);
        return authCodeEntity.getAccessToken();
    }
}
