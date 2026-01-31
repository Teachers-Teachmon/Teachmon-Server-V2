package solvit.teachmon.domain.user.domain.enums;

import lombok.RequiredArgsConstructor;
import solvit.teachmon.domain.auth.exception.UnsupportedOAuth2ProviderException;

import java.util.Arrays;

@RequiredArgsConstructor
public enum OAuth2Type {
    GOOGLE("google");

    private final String value;

    public static OAuth2Type of(String type) {
        return Arrays.stream(values())
                .filter(oAuth2Type -> oAuth2Type.value.equals(type))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOAuth2ProviderException(type));
    }
}
