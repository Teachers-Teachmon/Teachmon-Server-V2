package solvit.teachmon.domain.auth.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import solvit.teachmon.domain.auth.exception.InvalidAuthCodeInfoException;

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
        validateFields(authCode, accessToken, timeToLive);

        this.authCode = authCode;
        this.accessToken = accessToken;
        this.timeToLive = timeToLive;
    }

    private void validateFields(String authCode, String accessToken, Long timeToLive) {
        if(authCode == null || authCode.trim().isEmpty())
            throw new InvalidAuthCodeInfoException("인증 코드는 비어 있을 수 없습니다.");
        if(accessToken == null || accessToken.trim().isEmpty())
            throw new InvalidAuthCodeInfoException("액세스 토큰은 비어 있을 수 없습니다.");
        if(timeToLive == null || timeToLive < 0)
            throw new InvalidAuthCodeInfoException("TTL 값은 0 이상이어야 합니다.");
    }
}
