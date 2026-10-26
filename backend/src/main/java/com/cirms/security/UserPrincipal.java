package com.cirms.security;

import com.cirms.entity.Role;
import com.cirms.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String studentId;
    private final String email;
    private final String phone;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String fullName, String studentId, String email, String phone, String password, Role role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullName = fullName;
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        String roleName = user.getRole() != null ? user.getRole().name() : Role.STUDENT.name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        return new UserPrincipal(
                user.getId(),
                user.getFullName(),
                user.getStudentId(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword(),
                user.getRole(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return true;
    }
}
