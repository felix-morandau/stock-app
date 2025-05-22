package com.felix_morandau.stock_app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean checkClaims(String token){
        Claims claims = getAllClaimsFromToken(token);

        if (!"stock-app-backend".equals(claims.getIssuer())) {
            log.error("Invalid token issuer");
            return false;
        }

        if (claims.getExpiration().before(new Date())) {
            log.error("Token has expired");
            return false;
        }

        if (claims.getIssuedAt() == null || claims.getIssuedAt().after(new Date())) {
            log.error("Token issued at date is invalid");
            return false;
        }

        if (claims.get("email") == null || claims.get("role") == null) {
            log.error("Token claims are invalid: does not contain username and role");
            return false;
        }

        log.info("Token is valid. Email: {}, Role: {}",
                claims.get("email"), claims.get("role"));

        return true;
    }


    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("/login".equals(path) || ("/user".equals(path) && "POST".equals(method)) || "OPTIONS".equalsIgnoreCase(method)) {
            log.info("Skipping JWT filter for path: {} and method: {}", path, method);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Authorization header is missing or does not start with 'Bearer '");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        try {
            boolean isValid = checkClaims(token);
            if (!isValid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            filterChain.doFilter(request, response);

        } catch (JwtException | IOException | ServletException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
