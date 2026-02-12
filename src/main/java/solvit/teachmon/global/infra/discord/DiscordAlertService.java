package solvit.teachmon.global.infra.discord;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.exception.ErrorResponse;
import solvit.teachmon.global.properties.DiscordProperties;
import solvit.teachmon.global.security.user.TeachmonUserDetails;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscordAlertService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final DiscordProperties discordProperties;

    public void alertError(HttpServletRequest request, ErrorResponse errorResponse) {
        Map<String, Object> errorMessage = prepareErrorMessage(request, errorResponse);
        String preparedErrorMessage = objectMapper.writeValueAsString(errorMessage);
        DiscordAlertMessageDto discordAlertMessageDto = DiscordAlertMessageDto.builder()
                .content(preparedErrorMessage)
                .build();
        restClient.post()
                .uri(discordProperties.getWebhook())
                .contentType(MediaType.APPLICATION_JSON)
                .body(discordAlertMessageDto)
                .retrieve()
                .toBodilessEntity();
    }

    private Map<String, Object> prepareErrorMessage(HttpServletRequest request, ErrorResponse errorResponse) {
        RequesterInfo requester = resolveRequester();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("발생시각", LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString());
        payload.put("요청자", requester.toMap());
        payload.put("요청 URI", request.getRequestURI());
        payload.put("HTTP 메서드", request.getMethod());
        payload.put("쿼리스트링", request.getQueryString());
        payload.put("요청 파라미터", extractRequestParams(request));
        payload.put("요청 본문", extractRequestBody(request));
        payload.put("응답 상태코드", errorResponse.getStatus());
        payload.put("응답 본문", errorResponse.getMessage());
        return payload;
    }

    private RequesterInfo resolveRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return new RequesterInfo("guest", "guest", "guest");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof TeachmonUserDetails) {
            TeacherEntity teacher = ((TeachmonUserDetails) principal).teacherEntity();
            return new RequesterInfo(teacher.getName(), teacher.getMail(), teacher.getRole().getValue());
        }

        return new RequesterInfo(authentication.getName(), "unknown", joinAuthorities(authentication));
    }

    private String extractRequestParams(HttpServletRequest request) {
        if (request.getParameterMap().isEmpty()) {
            return "존재하지 않음";
        }

        return request.getParameterMap()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + joinValues(entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    private String extractRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper == null) {
            return "존재하지 않음";
        }

        byte[] content = wrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "존재하지 않음";
        }

        Charset charset = Optional.of(wrapper.getCharacterEncoding())
                .map(Charset::forName)
                .orElse(StandardCharsets.UTF_8);
        return new String(content, charset);
    }

    private String joinAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private String joinValues(String[] values) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        Arrays.stream(values).forEach(joiner::add);
        return joiner.toString();
    }

    private record RequesterInfo(String name, String email, String role) {
        Map<String, String> toMap() {
            return Map.of(
                    "이름", name,
                    "이메일", email,
                    "역할", role
            );
        }
    }
}
