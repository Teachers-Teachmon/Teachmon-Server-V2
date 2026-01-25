package solvit.teachmon.domain.auth.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.auth.application.service.AuthService;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.domain.auth.presentation.dto.request.AuthCodeRequestDto;
import solvit.teachmon.domain.auth.presentation.dto.response.TokenResponseDto;
import solvit.teachmon.global.constants.JwtConstants;

import java.util.Map;

/**
 * 인증 및 토큰 관리를 위한 REST API 컨트롤러
 * REST API Controller for authentication and token management
 * 
 * <p>OAuth2 기반 인증 흐름을 지원하며, 토큰 발급 및 갱신을 처리합니다.
 * Supports OAuth2-based authentication flow and handles token issuance and renewal.
 * 
 * <p>인증 흐름:
 * Authentication flow:
 * <ol>
 *   <li>사용자가 OAuth2 제공자(Google 등)를 통해 인증 (User authenticates via OAuth2 provider like Google)</li>
 *   <li>성공 핸들러가 AuthCode를 생성하고 프론트엔드로 리디렉션 (Success handler creates AuthCode and redirects to frontend)</li>
 *   <li>프론트엔드가 /auth/code로 AuthCode 전송 (Frontend sends AuthCode to /auth/code)</li>
 *   <li>서버가 Access Token(응답 바디) + Refresh Token(쿠키)을 반환 (Server returns Access Token in body and Refresh Token in cookie)</li>
 * </ol>
 * 
 * <p>보안 특성:
 * Security features:
 * <ul>
 *   <li>Refresh Token은 HttpOnly 쿠키로 전송되어 XSS 공격 방지 (Refresh Token in HttpOnly cookie prevents XSS)</li>
 *   <li>Access Token은 응답 바디로 전송되어 프론트엔드가 메모리에 저장 (Access Token in response body for frontend memory storage)</li>
 *   <li>Token Rotation: 재발급 시 기존 Refresh Token을 폐기하여 보안 강화 (Token rotation invalidates old refresh tokens for security)</li>
 * </ul>
 * 
 * @see AuthService
 * @see TokenResponseDto
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Access Token 재발급
     * Reissues access token using refresh token
     * 
     * <p>만료된 Access Token을 갱신하기 위해 사용합니다.
     * Used to renew expired access tokens.
     * 
     * <p>Token Rotation 적용: 보안을 위해 새 Refresh Token도 함께 발급하며 기존 Refresh Token은 폐기됩니다.
     * Token rotation applied: Issues new refresh token and invalidates the old one for security.
     * 
     * <p>응답 형태:
     * Response format:
     * <ul>
     *   <li>Body: {"access_token": "new_access_token"} (New access token in JSON body)</li>
     *   <li>Cookie: Set-Cookie: refresh_token=new_refresh_token; HttpOnly; Secure; SameSite=Lax (New refresh token in HttpOnly cookie)</li>
     * </ul>
     * 
     * @param refreshToken Refresh Token (쿠키에서 자동 추출) (Automatically extracted from cookie)
     * @return 새 Access Token (Body)과 새 Refresh Token (Cookie) (New access token in body and new refresh token in cookie)
     * @throws RefreshTokenNotFoundException Refresh Token이 없거나 유효하지 않은 경우 (If refresh token is missing or invalid)
     */
    @PostMapping("/reissue")
    public ResponseEntity<Map<String, String>> reissue(@CookieValue(value = JwtConstants.REFRESH_TOKEN_COOKIE_HEADER, required = false) String refreshToken) {
        if(refreshToken == null) throw new RefreshTokenNotFoundException();
        TokenResponseDto tokenResponseDto = authService.reissueToken(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenResponseDto.refreshTokenCookie().toString())
                .body(Map.of("access_token", tokenResponseDto.accessToken()));
    }

    /**
     * 로그아웃
     * Logs out the user by invalidating refresh token
     * 
     * <p>Redis에서 Refresh Token을 삭제하고, 클라이언트 쿠키도 삭제하도록 응답합니다.
     * Deletes the refresh token from Redis and responds with a cookie deletion directive.
     * 
     * <p>로그아웃 후:
     * After logout:
     * <ul>
     *   <li>Refresh Token은 Redis에서 완전히 삭제되어 재사용 불가 (Refresh token is permanently deleted from Redis)</li>
     *   <li>클라이언트의 쿠키가 만료되어 브라우저에서 제거됨 (Client cookie expires and is removed from browser)</li>
     *   <li>Access Token은 서버에 저장되지 않으므로 만료될 때까지 유효함 (Access token remains valid until expiration as it's not stored)</li>
     * </ul>
     * 
     * @param refreshToken Refresh Token (쿠키에서 자동 추출) (Automatically extracted from cookie)
     * @return 쿠키 삭제 지시가 포함된 빈 응답 (Empty response with cookie deletion directive)
     * @throws RefreshTokenNotFoundException Refresh Token이 없는 경우 (If refresh token is missing)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = JwtConstants.REFRESH_TOKEN_COOKIE_HEADER, required = false) String refreshToken) {
        if(refreshToken == null) throw new RefreshTokenNotFoundException();
        ResponseCookie deletedRefreshTokenCookie = authService.deleteRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deletedRefreshTokenCookie.toString())
                .build();
    }

    /**
     * Auth Code를 Access Token으로 교환
     * Exchanges auth code for access token
     * 
     * <p>OAuth2 인증 성공 후 프론트엔드가 받은 일회용 Auth Code를 실제 Access Token으로 교환합니다.
     * Exchanges the one-time auth code received by frontend after OAuth2 success for an actual access token.
     * 
     * <p>Auth Code 패턴 사용 이유:
     * Why use Auth Code pattern:
     * <ul>
     *   <li>OAuth2 리디렉션 URL에 토큰을 직접 노출하지 않음 (Avoids exposing tokens in OAuth2 redirect URL)</li>
     *   <li>일회용 코드로 토큰 탈취 위험 감소 (One-time code reduces token theft risk)</li>
     *   <li>프론트엔드 라우팅과 토큰 저장 로직 분리 가능 (Separates frontend routing from token storage logic)</li>
     * </ul>
     * 
     * <p>주의: Auth Code는 사용 후 즉시 Redis에서 삭제되므로 재사용 불가능합니다.
     * Note: Auth code is deleted from Redis immediately after use and cannot be reused.
     * 
     * @param authCodeRequestDto Auth Code가 포함된 요청 (Request containing the auth code)
     * @return Access Token (응답 바디에 JSON 형태로 반환) (Access token returned in JSON body)
     * @throws AuthCodeNotFoundException Auth Code가 없거나 만료된 경우 (If auth code is missing or expired)
     */
    @PostMapping("/code")
    public ResponseEntity<Map<String, String>> authCode(@RequestBody @Valid AuthCodeRequestDto authCodeRequestDto) {
        String accessToken = authService.getAccessTokenByAuthCode(authCodeRequestDto.code());
        return ResponseEntity.ok().body(Map.of("access_token", accessToken));
    }
}
