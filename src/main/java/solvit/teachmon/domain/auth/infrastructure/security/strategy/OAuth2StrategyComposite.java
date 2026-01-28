package solvit.teachmon.domain.auth.infrastructure.security.strategy;

import org.springframework.stereotype.Component;
import solvit.teachmon.domain.auth.exception.UnsupportedOAuth2ProviderException;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class OAuth2StrategyComposite {
    private final Map<OAuth2Type, OAuth2Strategy> oauth2ProviderMap;

    public OAuth2StrategyComposite(Set<OAuth2Strategy> clients) {
        this.oauth2ProviderMap = clients.stream()
                .collect(toMap(OAuth2Strategy::getOAuth2ProviderType, identity()));
    }

    public OAuth2Strategy getOAuth2Strategy(OAuth2Type providerType) {
        return Optional.ofNullable(oauth2ProviderMap.get(providerType))
                .orElseThrow(() -> new UnsupportedOAuth2ProviderException("not supported OAuth2 provider"));
    }
}