package solvit.teachmon.domain.user.domain.enums;

import lombok.RequiredArgsConstructor;
import solvit.teachmon.domain.auth.exception.UnsupportedOAuth2ProviderException;

import java.util.Arrays;

/**
 * OAuth2 인증 제공자 타입을 나타내는 열거형
 * Enumeration representing OAuth2 authentication provider types
 * 
 * <p>현재 지원하는 제공자:
 * Currently supported providers:
 * <ul>
 *   <li>GOOGLE: Google OAuth2 인증 (Google OAuth2 authentication)</li>
 * </ul>
 * 
 * <p>향후 확장 가능한 제공자:
 * Future extensible providers:
 * <ul>
 *   <li>NAVER: 네이버 OAuth2 인증 (Naver OAuth2 authentication)</li>
 *   <li>KAKAO: 카카오 OAuth2 인증 (Kakao OAuth2 authentication)</li>
 * </ul>
 * 
 * <p>새 제공자 추가 방법:
 * How to add a new provider:
 * <ol>
 *   <li>이 열거형에 새 상수 추가 (예: NAVER("naver")) (Add new constant to this enum)</li>
 *   <li>OAuth2Strategy 인터페이스 구현체 작성 (예: NaverOAuth2Strategy) (Implement OAuth2Strategy interface)</li>
 *   <li>application.yml에 OAuth2 클라이언트 설정 추가 (Add OAuth2 client configuration to application.yml)</li>
 * </ol>
 * 
 * @see solvit.teachmon.domain.auth.infrastructure.security.strategy.OAuth2Strategy
 * @see solvit.teachmon.domain.auth.infrastructure.security.strategy.OAuth2StrategyComposite
 */
@RequiredArgsConstructor
public enum OAuth2Type {
    /** Google OAuth2 제공자 (Google OAuth2 provider) */
    GOOGLE("google");

    /** OAuth2 제공자 식별자 (소문자) (OAuth2 provider identifier in lowercase) */
    private final String value;

    /**
     * 문자열 값으로 OAuth2Type 조회
     * Retrieves OAuth2Type by string value
     * 
     * <p>대소문자를 구분하여 정확히 일치하는 값을 찾습니다.
     * Searches for an exact match (case-sensitive).
     * 
     * @param type OAuth2 제공자 문자열 (예: "google") (OAuth2 provider string, e.g., "google")
     * @return 일치하는 OAuth2Type (Matching OAuth2Type)
     * @throws UnsupportedOAuth2ProviderException 지원하지 않는 제공자인 경우 (If provider is not supported)
     */
    public static OAuth2Type of(String type) {
        return Arrays.stream(values())
                .filter(oAuth2Type -> oAuth2Type.value.equals(type))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOAuth2ProviderException(type));
    }
}