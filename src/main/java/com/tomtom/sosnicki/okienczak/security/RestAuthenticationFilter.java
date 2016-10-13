package com.tomtom.sosnicki.okienczak.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public RestAuthenticationFilter() {
        this("login");
    }

    public RestAuthenticationFilter(String url) {
        this(makePostMatcher(url));
    }

    public RestAuthenticationFilter(RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException {
        final LoginRequest loginRequest = parseRequest(request);
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest
                = new UsernamePasswordAuthenticationToken(username, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private static RequestMatcher makePostMatcher(String url) {
        return new AntPathRequestMatcher(url, "POST");
    }

    private LoginRequest parseRequest(HttpServletRequest servletRequest) throws IOException {
        final BufferedReader reader = servletRequest.getReader();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(reader, LoginRequest.class);
    }

}
