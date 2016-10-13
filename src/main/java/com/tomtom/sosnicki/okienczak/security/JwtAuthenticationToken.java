package com.tomtom.sosnicki.okienczak.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object username;
    private String token;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.username = null;
        this.token = token;
    }

    public JwtAuthenticationToken(
            String username, String token, Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.username = username;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public void eraseCredentials() {
        token = null;
        super.eraseCredentials();
    }

}
