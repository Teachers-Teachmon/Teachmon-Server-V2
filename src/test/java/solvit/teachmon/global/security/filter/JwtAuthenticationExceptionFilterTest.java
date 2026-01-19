package solvit.teachmon.global.security.filter;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import solvit.teachmon.global.security.exception.InvalidJsonWebTokenException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT 인증 예외 필터 테스트")
class JwtAuthenticationExceptionFilterTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationExceptionFilter jwtAuthenticationExceptionFilter;

    @BeforeEach
    void setUp() {
        PathMatcher pathMatcher = new AntPathMatcher();
        String[] excludedPaths = new String[]{"/test"};
        jwtAuthenticationExceptionFilter = new JwtAuthenticationExceptionFilter(objectMapper, pathMatcher, excludedPaths);
    }

    private void setupMocksForErrorResponse() throws Exception {
        PrintWriter printWriter = new PrintWriter(new StringWriter());
        given(response.getWriter()).willReturn(printWriter);
        given(objectMapper.writeValueAsString(any())).willReturn("{}");
    }

    private void verifyErrorResponse(int expectedStatus) {
        verify(response).setStatus(expectedStatus);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    @DisplayName("JWT 토큰이 유효하지 않으면 에러 응답을 반환한다")
    void shouldReturnErrorResponseWhenInvalidJwtToken() throws Exception {
        // Given
        setupMocksForErrorResponse();
        willThrow(new InvalidJsonWebTokenException()).given(filterChain).doFilter(request, response);

        // When
        jwtAuthenticationExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verifyErrorResponse(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("예외가 발생하지 않으면 정상적으로 필터 체인을 진행한다")
    void shouldProceedFilterChainWhenNoException() throws Exception {
        // When
        jwtAuthenticationExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    @DisplayName("일반적인 예외가 발생하면 내부 서버 오류를 반환한다")
    void shouldReturnInternalServerErrorWhenGeneralException() throws Exception {
        // Given
        setupMocksForErrorResponse();
        willThrow(new RuntimeException("Unexpected error")).given(filterChain).doFilter(request, response);

        // When
        jwtAuthenticationExceptionFilter.doFilterInternal(request, response, filterChain);

        // Then
        verifyErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}