package com.merigasparyan.jmp.parkingserviceapi.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtTokenValidity;

    public static final String AUTH_TYPE = "Bearer ";
    private static final String ROLES = "roles";
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "access";

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtTokenValidity))
                .withIssuedAt(new Date())
                .withClaim(ROLES, userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withClaim(TOKEN_TYPE, ACCESS_TOKEN)
                .sign(getAlgorithm());
    }


    public String getUsername(String token) {
        return verifyAndDecode(token).getSubject();
    }

    public String[] getAuthorities(String token) {
        return verifyAndDecode(token).getClaim(ROLES).asArray(String.class);
    }

    public boolean isVerified(String token) {
        try {
            verifyAndDecode(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private DecodedJWT verifyAndDecode(String token) {
        return JWT.require(getAlgorithm()).build().verify(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = verifyAndDecode(token).getExpiresAt();
            return expiration.before(new Date());
        } catch (JWTVerificationException e) {
            return true;
        }
    }
}
