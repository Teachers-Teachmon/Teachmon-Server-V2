package solvit.teachmon.domain.auth.infrastructure.security.vo;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import solvit.teachmon.domain.user.domain.enums.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TeachmonOAuth2User implements OAuth2User {
    
    private final String mail;
    private final Role role;
    private final Map<String, Object> attributes;
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getValue()));
    }

    @NonNull
    @Override
    public String getName() {
        return mail;
    }

    @Builder
    public TeachmonOAuth2User(String mail, Role role,  Map<String, Object> attributes) {
        this.mail = mail;
        this.role = role;
        this.attributes = attributes;
    }
}
