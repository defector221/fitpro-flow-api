package com.fitpro.security;

import com.fitpro.domain.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final UUID gymId;
    private final UUID branchId;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.gymId = user.getGymId();
        this.branchId = user.getBranchId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.active = user.isActive();
        this.authorities = buildAuthorities(user);
    }

    private static List<GrantedAuthority> buildAuthorities(User user) {
        var roleAuthorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()));
        var permAuthorities = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> new SimpleGrantedAuthority(p.getCode()));
        return Stream.concat(roleAuthorities, permAuthorities)
                .collect(Collectors.toSet())
                .stream()
                .map(a -> (GrantedAuthority) a)
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
