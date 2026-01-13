package solvit.teachmon.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import solvit.teachmon.global.constants.JwtConstants;
import solvit.teachmon.global.security.jwt.JwtValidator;
import solvit.teachmon.global.security.user.TeachmonUserDetails;
import solvit.teachmon.global.security.user.TeachmonUserDetailsService;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtValidator jwtValidator;
    private final TeachmonUserDetailsService teachmonUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if(jwtValidator.isInvalidAuthorizationHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String mail = jwtValidator.getMailFromAuthorizationHeader(authHeader);
        TeachmonUserDetails teachmonUserDetails = teachmonUserDetailsService.loadUserByUsername(mail);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(teachmonUserDetails, null, teachmonUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
