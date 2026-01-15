package solvit.teachmon.domain.oauth2.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


@RedisHash(value = "token")
public class TokenEntity {
    @Id
    private String mail;

    @Column(unique = true, nullable = false)
    private String token;

    @TimeToLive
    private Long expiration;

    @Builder
    public TokenEntity(String mail, String token, Long expiration) {
        this.mail = mail;
        this.token = token;
        this.expiration = expiration;
    }
}
