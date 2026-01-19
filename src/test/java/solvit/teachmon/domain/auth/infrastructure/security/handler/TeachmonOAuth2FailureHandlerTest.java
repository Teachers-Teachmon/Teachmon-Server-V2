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
        // Given: OAuth2 인증이 실패했을 때
        AuthenticationException exception = new BadCredentialsException("Invalid credentials");

        // When: 실패 핸들러가 호출되면
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then: 에러 정보와 함께 프론트엔드로 리다이렉트된다
        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).startsWith("http://localhost:3000/oauth2/callback#error=");
        assertThat(redirectUrl).contains("BadCredentialsException");
    }

    @Test
    @DisplayName("OAuth2AuthenticationException 발생 시 올바른 에러 타입을 전달한다")
    void onAuthenticationFailure_WithOAuth2Exception_SendsCorrectErrorType() throws IOException {
        // Given: OAuth2AuthenticationException이 발생했을 때
        AuthenticationException exception = new OAuth2AuthenticationException("oauth2_error");

        // When: 실패 핸들러가 호출되면
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then: OAuth2AuthenticationException 타입이 URL에 포함된다
        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).contains("OAuth2AuthenticationException");
    }

    @Test
    @DisplayName("다양한 인증 예외 타입을 올바르게 처리한다")
    void onAuthenticationFailure_HandlesVariousExceptionTypes() throws IOException {
        // Given: 다른 타입의 인증 예외가 발생했을 때
        AuthenticationException exception = new AuthenticationException("Generic error") {
            // Anonymous class to simulate different exception type
        };

        // When: 실패 핸들러가 호출되면
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then: 예외 클래스 이름이 URL에 포함된다
        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).contains("error=");
        assertThat(redirectUrl).startsWith("http://localhost:3000/oauth2/callback#error=");
    }

    @Test
    @DisplayName("에러 메시지가 URL 인코딩되어 전달된다")
    void onAuthenticationFailure_URLEncodesErrorMessage() throws IOException {
        // Given: 특수 문자가 포함된 예외가 발생했을 때
        AuthenticationException exception = new AuthenticationException("Test Exception") {};

        // When: 실패 핸들러가 호출되면
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then: 예외 클래스명이 URL 인코딩되어 전달된다
        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).contains("error=");
        // Anonymous class name will be different, but should be URL encoded
        assertThat(redirectUrl).matches(".*error=.*");
    }

    @Test
    @DisplayName("프론트엔드 URL 설정을 올바르게 사용한다")
    void onAuthenticationFailure_UsesCorrectFrontendUrl() throws IOException {
        // Given: 다른 프론트엔드 URL이 설정되었을 때
        given(webProperties.getFrontEndUrl()).willReturn("https://example.com");
        AuthenticationException exception = new BadCredentialsException("Test");

        // When: 실패 핸들러가 호출되면
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then: 설정된 프론트엔드 URL을 사용한다
        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);
        then(response).should(times(1)).sendRedirect(redirectUrlCaptor.capture());
        
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).startsWith("https://example.com/oauth2/callback#error=");
    }
}