package com.tomtom.sosnicki.okienczak.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tomtom.sosnicki.okienczak.entity.AccountEntity;
import com.tomtom.sosnicki.okienczak.entity.AuthorityEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AccountDto {

    private Long id;
    private String username;
    private String password;
    private List<AuthorityEntity.Name> authorities;

    public AccountDto() {
    }

    public AccountDto(Long id, String username, String password, List<AuthorityEntity.Name> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public AccountDto(AccountEntity entity) {
        copyFromEntity(entity);
    }

    public AccountEntity toEntity(PasswordEncoder passwordEncoder) {
        final AccountEntity entity = new AccountEntity();
        applyToEntity(passwordEncoder, entity);
        return entity;
    }

    public void applyToEntity(PasswordEncoder passwordEncoder, AccountEntity entity) {
        if (username != null) {
            entity.setUsername(username);
        }
        if (password != null) {
            final String encodedPassword = passwordEncoder.encode(password);
            entity.setPassword(encodedPassword);
        }
        if (authorities != null) {
            final List<AuthorityEntity> authorities = entity.getAuthorities();
            final List<AuthorityEntity> newAuthorities = this.authorities.stream()
                    .map(authority -> new AuthorityEntity(authority, entity))
                    .collect(Collectors.toList());

            authorities.clear();
            authorities.addAll(newAuthorities);
        }
    }

    public void copyFromEntity(AccountEntity entity) {
        id = entity.getId();
        username = entity.getUsername();
        password = null;
        authorities = entity.getAuthorities().stream()
                .map(AuthorityEntity::getName)
                .collect(Collectors.toList());
    }

    public boolean areAuthoritiesSet() {
        return authorities != null;
    }

    public void unsetAuthorities() {
        authorities = null;
    }

    public void setAuthorities(AuthorityEntity.Name... names) {
        authorities = new ArrayList<>();
        authorities.addAll(Arrays.asList(names));
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<AuthorityEntity.Name> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
