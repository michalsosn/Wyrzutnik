package com.tomtom.sosnicki.okienczak.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ReturnJwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private Logger log = LoggerFactory.getLogger(ReturnJwtAuthenticationSuccessHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        final PrintWriter writer = response.getWriter();
        final TokenResponse token = jwtService.createTokenResponse(authentication);

        log.info(String.format("Returning token %s on successful login.", token));

        objectMapper.writeValue(writer, token);
        clearAuthenticationAttributes(request);
    }
}
