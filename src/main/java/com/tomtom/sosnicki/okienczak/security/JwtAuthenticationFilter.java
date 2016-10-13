package com.tomtom.sosnicki.okienczak.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authentication";
    private static final String HEADER_HEAD = "Bearer ";

    private AuthenticationManager authenticationManager;
    private RequestMatcher requiresAuthenticationRequestMatcher;

    public JwtAuthenticationFilter(String antPattern) {
        requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(antPattern);
    }

    public JwtAuthenticationFilter(RequestMatcher requestMatcher) {
        requiresAuthenticationRequestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        if (authenticationRequired(request)) {
            final Authentication authenticationResult = attemptAuthentication(request);
            if (authenticationResult != null) {
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Authentication attemptAuthentication(HttpServletRequest request) {
        final String header = request.getHeader(HEADER);

        if (header == null || !header.startsWith(HEADER_HEAD)) {
            return null;
        }

        final String token = header.substring(HEADER_HEAD.length());

        final JwtAuthenticationToken authenticationRequest = new JwtAuthenticationToken(token);

        return getAuthenticationManager().authenticate(authenticationRequest);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public RequestMatcher getRequiresAuthenticationRequestMatcher() {
        return requiresAuthenticationRequestMatcher;
    }

    public void setRequiresAuthenticationRequestMatcher(RequestMatcher requiresAuthenticationRequestMatcher) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }

    private boolean authenticationRequired(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }
}
