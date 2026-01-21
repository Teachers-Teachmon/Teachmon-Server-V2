package solvit.teachmon.domain.auth.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import solvit.teachmon.domain.auth.exception.InvalidTokenInfoException;


@Getter
@RedisHash(value = "token")
public class TokenEntity {
    @Id
    private final String token;

    private final String mail;

    @TimeToLive
    private final Long expiration;

    @Builder
    public TokenEntity(String mail, String token, Long expiration) {
        this.mail = mail;
        this.token = token;
        this.expiration = expiration;
    }

    private void validateFields(String mail, String token, Long expiration) {
        if(mail == null || mail.trim().isEmpty())
            throw new InvalidTokenInfoException("메일은 비어 있을 수 없습니다.");
        if(token == null || token.trim().isEmpty())
            throw new InvalidTokenInfoException("토큰은 비어 있을 수 없습니다.");
        if(expiration == null || expiration < 0)
            throw new InvalidTokenInfoException("expiration 값은 0 이상이어야 합니다.");
    }
}
