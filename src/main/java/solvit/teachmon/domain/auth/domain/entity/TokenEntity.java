package solvit.teachmon.domain.auth.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


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
}
