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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import solvit.teachmon.global.properties.WebProperties;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("티치몬 OAuth2 실패 핸들러 테스트")
class TeachmonOAuth2FailureHandlerTest {

    @Mock
    private WebProperties webProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TeachmonOAuth2FailureHandler failureHandler;

    @BeforeEach
    void setUp() {
        given(webProperties.getFrontEndUrl()).willReturn("http://localhost:3000");
    }

    @Test
    @DisplayName("OAuth2 인증 실패 시 에러 메시지와 함께 프론트엔드로 리다이렉트한다")
    void onAuthenticationFailure_RedirectsWithError() throws IOException {
        AuthenticationException exception = new BadCredentialsException("Invalid credentials");

        failureHandler.onAuthenticationFailure(request, response, exception);

        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).startsWith("http://localhost:3000/oauth2/callback#error=");
        assertThat(redirectUrl).contains("BadCredentialsException");
    }

    @Test
    @DisplayName("OAuth2AuthenticationException 발생 시 올바른 에러 타입을 전달한다")
    void onAuthenticationFailure_WithOAuth2Exception_SendsCorrectErrorType() throws IOException {
        AuthenticationException exception = new OAuth2AuthenticationException("oauth2_error");

        failureHandler.onAuthenticationFailure(request, response, exception);

        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).contains("OAuth2AuthenticationException");
    }

    @Test
    @DisplayName("다양한 인증 예외 타입을 올바르게 처리한다")
    void onAuthenticationFailure_HandlesVariousExceptionTypes() throws IOException {
        AuthenticationException exception = new AuthenticationException("Generic error") {
        };

        failureHandler.onAuthenticationFailure(request, response, exception);

        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).contains("error=");
        assertThat(redirectUrl).startsWith("http://localhost:3000/oauth2/callback#error=");
    }

    @Test
    @DisplayName("에러 메시지가 URL 인코딩되어 전달된다")
    void onAuthenticationFailure_URLEncodesErrorMessage() throws IOException {
        AuthenticationException exception = new AuthenticationException("Test Exception") {};

        failureHandler.onAuthenticationFailure(request, response, exception);

        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).contains("error=");
        assertThat(redirectUrl).matches(".*error=.*");
    }

    @Test
    @DisplayName("프론트엔드 URL 설정을 올바르게 사용한다")
    void onAuthenticationFailure_UsesCorrectFrontendUrl() throws IOException {
        given(webProperties.getFrontEndUrl()).willReturn("https://example.com");
        AuthenticationException exception = new BadCredentialsException("Test");

        failureHandler.onAuthenticationFailure(request, response, exception);

        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).startsWith("https://example.com/oauth2/callback#error=");
    }
}
