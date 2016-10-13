package com.tomtom.sosnicki.okienczak.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Pattern AUTHORITY_DELIMITER = Pattern.compile(",");
    private static final String CLAIMS_AUTHORITIES = "aut";

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private Clock clock;

    public TokenResponse createTokenResponse(Authentication authentication) {
        return createTokenResponse(authentication, null);
    }

    public TokenResponse createTokenResponse(Authentication authentication, Duration expirationDuration) {
        final Map<String, Object> claims = new HashMap<>();

        final String username = authentication.getName();
        claims.put(Claims.SUBJECT, username);

        final Instant currentTime = clock.instant();
        claims.put(Claims.ISSUED_AT, currentTime.getEpochSecond());
        Instant expirationTime = null;
        if (expirationDuration != null) {
            expirationTime = currentTime.plus(expirationDuration);
            claims.put(Claims.EXPIRATION, expirationTime.getEpochSecond());
        }

        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(AUTHORITY_DELIMITER.pattern()));
        claims.put(CLAIMS_AUTHORITIES, authorities);

        final String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return new TokenResponse(username, token, expirationTime);
    }

    public JwtAuthenticationToken parseToken(String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        final String username = claims.getSubject();

        final String rolesString = claims.get(CLAIMS_AUTHORITIES, String.class);
        final List<SimpleGrantedAuthority> authorities = AUTHORITY_DELIMITER.splitAsStream(rolesString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(username, token, authorities);
    }

}
