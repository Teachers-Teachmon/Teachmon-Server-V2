package solvit.teachmon.domain.auth.infrastructure.security.vo;

import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

public record TeachmonOAuth2UserInfo(
    String providerId,
    String mail,
    String profile,
    String name,
    OAuth2Type oAuth2Type
) { }
