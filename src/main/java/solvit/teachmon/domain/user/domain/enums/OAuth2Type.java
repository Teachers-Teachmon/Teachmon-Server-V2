package solvit.teachmon.domain.auth.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Type {
    GOOGLE("Google");

    private final String value;
}