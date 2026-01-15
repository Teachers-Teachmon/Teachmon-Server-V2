package solvit.teachmon.global.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.constants.HttpResponseConstants;
import solvit.teachmon.global.exception.ErrorResponse;
import solvit.teachmon.global.security.exception.InvalidJsonWebTokenException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final String[] excludedPaths;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch(TeacherNotFoundException e) {
            handleAuthException(response, HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(InvalidJsonWebTokenException e) {
            handleAuthException(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        catch(AuthenticationException e) {
            handleAuthException(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        catch(AccessDeniedException e) {
            handleAuthException(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
        catch(ExpiredJwtException e) {
            handleAuthException(response, HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        }
        catch (JwtException e) {
            log.warn("JwtException 발생 : {}", e.getMessage());
            handleAuthException(response, HttpStatus.UNAUTHORIZED, "인증과정 중 오류가 발생했습니다.");
        }
        catch (Exception e) {
            log.error("예상치 못한 오류 발생 : {}", e.getMessage());
            handleAuthException(response, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다.");
        }
    }

    private void handleAuthException(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        SecurityContextHolder.clearContext();

        response.setStatus(httpStatus.value());
        response.setContentType(HttpResponseConstants.CONTENT_TYPE);
        response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);

        ErrorResponse errorResponse = ErrorResponse.of(httpStatus.value(), message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return Arrays.stream(excludedPaths)
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
    }
}
