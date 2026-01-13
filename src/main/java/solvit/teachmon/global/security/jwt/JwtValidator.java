package solvit.teachmon.global.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import solvit.teachmon.global.constants.JwtConstants;
import solvit.teachmon.global.properties.JwtProperties;
import solvit.teachmon.global.security.exception.InvalidJsonWebTokenException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class JwtValidator {
    private final SecretKey secretKey;

    @Autowired
    public JwtValidator(JwtProperties jwtProperties) {
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    private String getNicknameFromToken(String token) {
        validateAccessToken(token);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getNicknameFromAuthorizationHeader(String authorizationHeader) {
        String accessToken = authorizationHeader.substring(JwtConstants.AUTHORIZATION_HEADER_PREFIX.length());

        return getNicknameFromToken(accessToken);
    }

    public boolean isInvalidAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader == null || !authorizationHeader.startsWith(JwtConstants.AUTHORIZATION_HEADER_PREFIX);
    }

    private void validateAccessToken(String accessToken) {
        String madeBy = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload().get(JwtConstants.CLAIM_MADY_BY_KEY, String.class);
        if(!madeBy.equals(JwtConstants.CLAIM_MADY_BY_VALUE)) throw new InvalidJsonWebTokenException();
    }
}
