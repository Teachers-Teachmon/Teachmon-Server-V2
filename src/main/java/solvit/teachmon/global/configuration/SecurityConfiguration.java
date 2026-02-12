package solvit.teachmon.global.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import solvit.teachmon.domain.auth.infrastructure.security.facade.TeachmonOAuth2UserFacade;
import solvit.teachmon.domain.auth.infrastructure.security.handler.TeachmonOAuth2FailureHandler;
import solvit.teachmon.domain.auth.infrastructure.security.handler.TeachmonOAuth2SuccessHandler;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.global.properties.WebProperties;
import solvit.teachmon.global.security.filter.JwtAuthenticationExceptionFilter;
import solvit.teachmon.global.security.filter.JwtAuthenticationFilter;
import solvit.teachmon.global.security.jwt.JwtValidator;
import solvit.teachmon.global.security.user.TeachmonUserDetailsService;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ObjectMapper objectMapper;
    private final JwtValidator jwtValidator;
    private final WebProperties webProperties;
    private final TeachmonUserDetailsService teachmonUserDetailsService;
    private final TeachmonOAuth2UserFacade teachmonOAuth2UserFacade;
    private final TeachmonOAuth2SuccessHandler teachmonOAuth2SuccessHandler;
    private final TeachmonOAuth2FailureHandler teachmonOAuth2FailureHandler;
    private static final String[] EXCLUDED_PATHS = {
            "/auth/reissue",
            "/auth/code",
            "/oauth2/callback/**",
            "/oauth2/login/**",
            "/actuator/**",
            "/test/error"
    };

    @Bean
    public PathMatcher antPathMatcher() {return new AntPathMatcher();}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EXCLUDED_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(anonymous -> anonymous
                        .principal(Role.GUEST.name())
                        .authorities(Role.GUEST.getValue())
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(webProperties.getFrontEndUrl() + "/oauth2")
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/login")
                        )
                        .successHandler(teachmonOAuth2SuccessHandler)
                        .failureHandler(teachmonOAuth2FailureHandler)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(teachmonOAuth2UserFacade)
                        )
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtValidator, teachmonUserDetailsService, antPathMatcher(), EXCLUDED_PATHS), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationExceptionFilter(objectMapper, antPathMatcher(), EXCLUDED_PATHS), JwtAuthenticationFilter.class);

        return http.build();
    }
}
