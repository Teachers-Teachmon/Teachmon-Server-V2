package solvit.teachmon.domain.oauth2.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.oauth2.domain.entity.TokenEntity;
import solvit.teachmon.domain.oauth2.domain.repository.TokenRepository;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void saveToken(String token, String mail, Long expiration) {
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(token)
                .mail(mail)
                .expiration(expiration)
                .build();
        tokenRepository.save(tokenEntity);
    }
}
