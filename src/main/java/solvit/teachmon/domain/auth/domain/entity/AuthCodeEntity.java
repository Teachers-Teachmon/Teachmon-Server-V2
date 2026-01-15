package solvit.teachmon.domain.auth.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("code")
public class AuthCodeEntity {
    @Id
    private final String authCode;

    private final String accessToken;

    @TimeToLive
    private final Long timeToLive;

    @Builder
    public AuthCodeEntity(String authCode, String accessToken, Long timeToLive) {
        this.authCode = authCode;
        this.accessToken = accessToken;
        this.timeToLive = timeToLive;
    }
}
