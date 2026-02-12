package solvit.teachmon.global.infra.discord;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record DiscordAlertMessageDto(
        @NonNull String content
) {}
