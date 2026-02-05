package solvit.teachmon.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"),
    TEACHER("ROLE_TEACHER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    public boolean isContains(List<Role> roles) {
        return roles != null && roles.contains(this);
    }
}