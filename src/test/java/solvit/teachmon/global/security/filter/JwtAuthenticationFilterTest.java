package solvit.teachmon.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.global.security.jwt.JwtValidator;
import solvit.teachmon.global.security.user.TeachmonUserDetails;
import solvit.teachmon.global.security.user.TeachmonUserDetailsService;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT 인증 필터 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private TeachmonUserDetailsService teachmonUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private TeachmonUserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtValidator, teachmonUserDetailsService, new String[]{"test"});

        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("수학 선생님")
                .providerId("google-12345")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        userDetails = new TeachmonUserDetails(teacher);
    }

    private void setupValidTokenMocks() {
        given(jwtValidator.isInvalidAuthorizationHeader("Bearer valid-token")).willReturn(false);
        given(jwtValidator.getMailFromAuthorizationHeader("Bearer valid-token")).willReturn("kim@teacher.com");
        given(teachmonUserDetailsService.loadUserByUsername("kim@teacher.com")).willReturn(userDetails);
    }

    private void verifySuccessfulAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(Objects.requireNonNull(authentication.getPrincipal())).isEqualTo(userDetails);
        assertThat(authentication.isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증에 성공한다")
    void shouldAuthenticateWithValidJwtToken() throws Exception {
        // Given
        given(request.getHeader("Authorization")).willReturn("Bearer valid-token");
        setupValidTokenMocks();

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verifySuccessfulAuthentication();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증을 건너뛴다")
    void shouldSkipAuthenticationWhenNoAuthorizationHeader() throws Exception {
        // Given
        given(request.getHeader("Authorization")).willReturn(null);
        given(jwtValidator.isInvalidAuthorizationHeader(null)).willReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtValidator, never()).getMailFromAuthorizationHeader(any());
    }

    @Test
    @DisplayName("유효하지 않은 Authorization 헤더면 인증을 건너뛴다")
    void shouldSkipAuthenticationWhenInvalidAuthorizationHeader() throws Exception {
        // Given
        given(request.getHeader("Authorization")).willReturn("Basic invalid-token");
        given(jwtValidator.isInvalidAuthorizationHeader("Basic invalid-token")).willReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtValidator, never()).getMailFromAuthorizationHeader(any());
    }
}