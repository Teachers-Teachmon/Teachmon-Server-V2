package solvit.teachmon.domain.auth.domain.entity;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("code")
public class AuthCodeEntity {
}
