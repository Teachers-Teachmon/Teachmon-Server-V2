package solvit.teachmon.global.security.user;

import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Collection;
import java.util.Collections;

public record TeachmonUserDetails(TeacherEntity teacherEntity) implements UserDetails {

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(teacherEntity.getRole().getValue()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @NonNull
    @Override
    public String getUsername() {
        return teacherEntity.getName();
    }

    @Override
    public boolean isEnabled() {
        return teacherEntity.getIsActive();
    }
}