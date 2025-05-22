package com.felix_morandau.stock_app.util;

import com.felix_morandau.stock_app.entity.transactional.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    protected SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {

        return Jwts
                .builder()
                .claims(
                        Map.of(
                                "email", user.getEmail(),
                                "role", user.getType()
                        )
                )
                .subject(user.getEmail())
                .issuer("stock-app-backend")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }
}
