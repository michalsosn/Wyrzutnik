package com.tomtom.sosnicki.okienczak.security;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new IllegalArgumentException("Passed authentication is not a JwtAuthenticationToken");
        }
        JwtAuthenticationToken jtwAuthentication = (JwtAuthenticationToken) authentication;

        final String token = jtwAuthentication.getCredentials();

        try {
            return jwtService.parseToken(token);
        } catch (MalformedJwtException exception) {
            throw new BadCredentialsException("Parsing token failed", exception);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class
                .isAssignableFrom(authentication));
    }

}
