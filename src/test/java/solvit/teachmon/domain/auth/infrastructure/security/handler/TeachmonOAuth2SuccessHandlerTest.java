package solvit.teachmon.domain.auth.infrastructure.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;
import solvit.teachmon.domain.auth.domain.repository.AuthCodeRepository;
import solvit.teachmon.domain.auth.infrastructure.jwt.JwtManager;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2User;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.global.properties.AuthCodeProperties;
import solvit.teachmon.global.properties.WebProperties;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("티치몬 OAuth2 성공 핸들러 테스트")
class TeachmonOAuth2SuccessHandlerTest {

    @Mock
    private JwtManager jwtManager;

    @Mock
    private AuthCodeProperties authCodeProperties;

    @Mock
    private AuthCodeRepository authCodeRepository;

    @Mock
    private WebProperties webProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TeachmonOAuth2SuccessHandler successHandler;

    private TeachmonOAuth2User oauth2User;

    @BeforeEach
    void setUp() {
        oauth2User = TeachmonOAuth2User.builder()
                .mail("test@example.com")
                .role(Role.TEACHER)
                .attributes(Map.of("id", "google-12345"))
                .build();

        given(authCodeProperties.getExpiration()).willReturn(300000L);
        given(webProperties.getFrontEndUrl()).willReturn("http://localhost:3000");
    }

    @Test
    @DisplayName("OAuth2 인증 성공 시 액세스 토큰과 리프레시 토큰을 생성한다")
    void onAuthenticationSuccess_CreatesTokens() throws IOException {
        // Given: OAuth2 인증이 성공했을 때
        String accessToken = "test-access-token";
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "test-refresh-token").build();
        
        given(authentication.getPrincipal()).willReturn(oauth2User);
        given(jwtManager.createAccessToken("test@example.com")).willReturn(accessToken);
        given(jwtManager.createRefreshToken("test@example.com")).willReturn(refreshCookie);

        // When: 성공 핸들러가 호출되면
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then: 토큰들이 생성된다
        then(jwtManager).should(times(1)).createAccessToken("test@example.com");
        then(jwtManager).should(times(1)).createRefreshToken("test@example.com");
    }

    @Test
    @DisplayName("OAuth2 인증 성공 시 인증 코드를 생성하고 저장한다")
    void onAuthenticationSuccess_CreatesAndSavesAuthCode() throws IOException {
        // Given: OAuth2 인증이 성공했을 때
        String accessToken = "test-access-token";
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "test-refresh-token").build();
        
        given(authentication.getPrincipal()).willReturn(oauth2User);
        given(jwtManager.createAccessToken(anyString())).willReturn(accessToken);
        given(jwtManager.createRefreshToken(anyString())).willReturn(refreshCookie);

        // When: 성공 핸들러가 호출되면
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then: 인증 코드가 생성되고 저장된다
        ArgumentCaptor<AuthCodeEntity> authCodeCaptor = ArgumentCaptor.forClass(AuthCodeEntity.class);
        then(authCodeRepository).should(times(1)).save(authCodeCaptor.capture());
        
        AuthCodeEntity savedAuthCode = authCodeCaptor.getValue();
        assertThat(savedAuthCode.getAuthCode()).isNotNull();
        assertThat(savedAuthCode.getAccessToken()).isEqualTo(accessToken);
        assertThat(savedAuthCode.getTimeToLive()).isEqualTo(300000L);
    }

    @Test
    @DisplayName("OAuth2 인증 성공 시 리프레시 토큰 쿠키를 설정한다")
    void onAuthenticationSuccess_SetsRefreshTokenCookie() throws IOException {
        // Given: OAuth2 인증이 성공했을 때
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "test-refresh-token")
                .httpOnly(true)
                .secure(true)
                .build();
        
        given(authentication.getPrincipal()).willReturn(oauth2User);
        given(jwtManager.createAccessToken(anyString())).willReturn("test-access-token");
        given(jwtManager.createRefreshToken(anyString())).willReturn(refreshCookie);

        // When: 성공 핸들러가 호출되면
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then: Set-Cookie 헤더가 추가된다
        then(response).should(times(1)).addHeader("Set-Cookie", refreshCookie.toString());
    }

    @Test
    @DisplayName("OAuth2 인증 성공 시 프론트엔드로 리다이렉트한다")
    void onAuthenticationSuccess_RedirectsToFrontend() throws IOException {
        // Given: OAuth2 인증이 성공했을 때
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "test-refresh-token").build();
        
        given(authentication.getPrincipal()).willReturn(oauth2User);
        given(jwtManager.createAccessToken(anyString())).willReturn("test-access-token");
        given(jwtManager.createRefreshToken(anyString())).willReturn(refreshCookie);

        // When: 성공 핸들러가 호출되면
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then: 프론트엔드 URL로 리다이렉트된다
        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).startsWith("http://localhost:3000/oauth2/callback#code=");
        assertThat(redirectUrl).contains("code=");
    }

    @Test
    @DisplayName("OAuth2User의 메일 정보를 올바르게 추출한다")
    void onAuthenticationSuccess_ExtractsCorrectMail() throws IOException {
        // Given: OAuth2User가 특정 메일을 가지고 있을 때
        TeachmonOAuth2User customUser = TeachmonOAuth2User.builder()
                .mail("custom@example.com")
                .role(Role.TEACHER)
                .attributes(Map.of("id", "google-67890"))
                .build();
        
        given(authentication.getPrincipal()).willReturn(customUser);
        given(jwtManager.createAccessToken("custom@example.com")).willReturn("test-token");
        given(jwtManager.createRefreshToken("custom@example.com"))
                .willReturn(ResponseCookie.from("refresh_token", "test").build());

        // When: 성공 핸들러가 호출되면
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then: 올바른 메일로 토큰이 생성된다
        then(jwtManager).should(times(1)).createAccessToken("custom@example.com");
        then(jwtManager).should(times(1)).createRefreshToken("custom@example.com");
    }
}