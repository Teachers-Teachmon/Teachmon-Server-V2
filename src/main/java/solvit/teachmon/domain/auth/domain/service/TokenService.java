package solvit.teachmon.domain.auth.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.auth.domain.entity.TokenEntity;
import solvit.teachmon.domain.auth.domain.repository.TokenRepository;

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

    public void deleteToken(String token) {
        tokenRepository.deleteById(token);
    }

    public boolean isInvalidToken(String token) {
        return !tokenRepository.existsById(token);
    }
}
