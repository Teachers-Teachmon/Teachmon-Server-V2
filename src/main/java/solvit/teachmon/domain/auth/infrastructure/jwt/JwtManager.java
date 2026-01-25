package solvit.teachmon.domain.auth.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.auth.domain.entity.TokenEntity;
import solvit.teachmon.domain.auth.domain.repository.TokenRepository;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.global.annotation.Trace;
import solvit.teachmon.global.constants.JwtConstants;
import solvit.teachmon.global.properties.JwtProperties;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * JWT 토큰 생성 및 관리를 담당하는 컴포넌트
 * Component responsible for JWT token creation and management
 * 
 * <p>주요 기능:
 * Main features:
 * <ul>
 *   <li>Access Token 생성 (JWT 형태, 응답 바디로 전송) (Creates access tokens in JWT format for response body)</li>
 *   <li>Refresh Token 생성 (JWT 형태, HttpOnly 쿠키로 전송) (Creates refresh tokens in JWT format for HttpOnly cookie)</li>
 *   <li>Refresh Token을 Redis에 저장 및 삭제 (Stores and deletes refresh tokens in Redis)</li>
 * </ul>
 * 
 * <p>보안 설정:
 * Security settings:
 * <ul>
 *   <li>HMAC SHA-256 서명 알고리즘 사용 (Uses HMAC SHA-256 signing algorithm)</li>
 *   <li>Refresh Token 쿠키: HttpOnly, Secure, SameSite=Strict (Refresh token cookie: HttpOnly, Secure, SameSite=Strict)</li>
 *   <li>HttpOnly: JavaScript 접근 차단으로 XSS 공격 방지 (HttpOnly prevents JavaScript access to prevent XSS)</li>
 *   <li>Secure: HTTPS 연결에서만 전송 (Secure ensures transmission over HTTPS only)</li>
 *   <li>SameSite=Strict: CSRF 공격 방지 (SameSite=Strict prevents CSRF attacks)</li>
 *   <li>Custom claim "made-by" 추가로 토큰 위조 방지 (Custom claim "made-by" prevents token forgery)</li>
 * </ul>
 * 
 * @see JwtValidator
 * @see TokenEntity
 * @see JwtProperties
 */
@Component
public class JwtManager {
    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;
    private final SecretKey secretKey;

    /**
     * JwtManager 생성자
     * 
     * <p>Secret Key를 HMAC SHA-256 알고리즘용 SecretKeySpec으로 초기화합니다.
     * Initializes the secret key as SecretKeySpec for HMAC SHA-256 algorithm.
     * 
     * @param jwtProperties JWT 설정 (만료 시간, Secret Key) (JWT configuration including expiration times and secret key)
     * @param tokenRepository Refresh Token 저장소 (Redis) (Refresh token repository using Redis)
     */
    @Autowired
    public JwtManager(JwtProperties jwtProperties, TokenRepository tokenRepository) {
        this.jwtProperties = jwtProperties;
        this.tokenRepository = tokenRepository;
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    /**
     * Access Token 생성
     * Creates an access token
     * 
     * <p>Access Token은 짧은 만료 시간을 가지며, 응답 바디(JSON)로 전송됩니다.
     * Access tokens have a short expiration time and are sent in the response body (JSON).
     * 
     * <p>포함된 Claims:
     * Included claims:
     * <ul>
     *   <li>subject: 사용자 이메일 (User email)</li>
     *   <li>made-by: 커스텀 claim으로 토큰 위조 방지 (Custom claim to prevent token forgery)</li>
     *   <li>issuedAt: 토큰 발급 시간 (Token issue time)</li>
     *   <li>expiration: 토큰 만료 시간 (Token expiration time)</li>
     * </ul>
     * 
     * @param mail 사용자 이메일 (토큰의 subject로 사용) (User email to be used as token subject)
     * @return JWT Access Token 문자열 (JWT access token string)
     */
    @Trace
    public String createAccessToken(String mail) {
        long now = System.currentTimeMillis();
        Date expiration = new Date(now + jwtProperties.getAccessExpiration());
        
        return Jwts.builder()
                .subject(mail)
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, JwtConstants.CLAIM_MADE_BY_VALUE)
                .issuedAt(new Date(now))
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성 및 HttpOnly 쿠키로 반환
     * Creates refresh token and returns as HttpOnly cookie
     * 
     * <p>Refresh Token은 긴 만료 시간을 가지며, Redis에 저장되고 HttpOnly 쿠키로 전송됩니다.
     * Refresh tokens have a long expiration time, are stored in Redis, and sent as HttpOnly cookies.
     * 
     * <p>보안 설정:
     * Security settings:
     * <ul>
     *   <li>httpOnly(true): JavaScript 접근 차단 (XSS 방지) (Blocks JavaScript access to prevent XSS)</li>
     *   <li>secure(true): HTTPS에서만 전송 (MITM 공격 방지) (Transmits only over HTTPS to prevent MITM attacks)</li>
     *   <li>sameSite("Strict"): 크로스 사이트 요청에서 쿠키 전송 금지 (CSRF 방지) (Prevents cookie transmission in cross-site requests to prevent CSRF)</li>
     *   <li>path("/"): 모든 경로에서 쿠키 사용 가능 (Cookie available for all paths)</li>
     * </ul>
     * 
     * <p>Redis 저장 이유:
     * Why store in Redis:
     * <ul>
     *   <li>로그아웃 시 즉시 토큰 무효화 가능 (Enables immediate token invalidation on logout)</li>
     *   <li>Token Rotation 구현 가능 (재발급 시 기존 토큰 삭제) (Enables token rotation - delete old token on reissue)</li>
     *   <li>TTL 기능으로 만료된 토큰 자동 삭제 (Auto-deletion of expired tokens via TTL)</li>
     * </ul>
     * 
     * @param mail 사용자 이메일 (User email)
     * @return Refresh Token이 포함된 ResponseCookie (ResponseCookie containing the refresh token)
     */
    public ResponseCookie createRefreshToken(String mail) {
        long now = System.currentTimeMillis();
        Date expiration = new Date(now + jwtProperties.getRefreshExpiration());

        String refreshToken = Jwts.builder()
                .subject(mail)
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, JwtConstants.CLAIM_MADE_BY_VALUE)
                .issuedAt(new Date(now))
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        saveRefreshToken(refreshToken, mail);

        return ResponseCookie.from(JwtConstants.REFRESH_TOKEN_COOKIE_HEADER, refreshToken)
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshExpiration()))
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    /**
     * Refresh Token 쿠키 삭제 응답 생성
     * Creates response to delete refresh token cookie
     * 
     * <p>로그아웃 시 사용됩니다. Redis에서 토큰을 삭제하고, 클라이언트 쿠키도 삭제하도록 지시합니다.
     * Used during logout. Deletes token from Redis and instructs client to delete the cookie.
     * 
     * <p>쿠키 삭제 방법: maxAge(0)으로 설정하여 브라우저가 즉시 쿠키를 제거하도록 합니다.
     * Cookie deletion method: Sets maxAge(0) to instruct browser to immediately remove the cookie.
     * 
     * @param refreshToken 삭제할 Refresh Token (Refresh token to delete)
     * @return maxAge=0으로 설정된 ResponseCookie (ResponseCookie with maxAge=0)
     * @throws RefreshTokenNotFoundException Refresh Token이 Redis에 없는 경우 (If refresh token doesn't exist in Redis)
     */
    public ResponseCookie deleteRefreshTokenCookie(String refreshToken) {
        deleteRefreshToken(refreshToken);

        return ResponseCookie.from(JwtConstants.REFRESH_TOKEN_COOKIE_HEADER, refreshToken)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    /**
     * Redis에서 Refresh Token 삭제
     * Deletes refresh token from Redis
     * 
     * <p>Token Rotation 구현의 핵심: 재발급 시 기존 토큰을 삭제하여 한 번에 하나의 유효한 토큰만 존재하도록 합니다.
     * Core of token rotation implementation: Deletes old token on reissue to ensure only one valid token exists at a time.
     * 
     * @param refreshToken 삭제할 Refresh Token (Refresh token to delete)
     * @throws RefreshTokenNotFoundException Refresh Token이 Redis에 없는 경우 (If refresh token doesn't exist in Redis)
     */
    public void deleteRefreshToken(String refreshToken) {
        if(!tokenRepository.existsById(refreshToken)) throw new RefreshTokenNotFoundException();
        tokenRepository.deleteById(refreshToken);
    }

    /**
     * Refresh Token을 Redis에 저장
     * Saves refresh token to Redis
     * 
     * <p>TokenEntity는 @RedisHash로 설정되어 Redis에 저장되며, TTL(Time To Live) 기능으로 자동 만료됩니다.
     * TokenEntity is configured with @RedisHash for Redis storage and auto-expires via TTL (Time To Live).
     * 
     * @param refreshToken JWT Refresh Token 문자열 (JWT refresh token string)
     * @param mail 사용자 이메일 (User email)
     */
    private void saveRefreshToken(String refreshToken, String mail) {
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(refreshToken)
                .mail(mail)
                .expiration(jwtProperties.getRefreshExpiration())
                .build();
        tokenRepository.save(tokenEntity);
    }
}
