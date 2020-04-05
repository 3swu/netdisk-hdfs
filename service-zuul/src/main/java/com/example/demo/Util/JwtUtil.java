package com.example.demo.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private String sign_key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.sign_key = secret;
    }

    public String createJWT(Date exp, Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date(System.currentTimeMillis());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signatureAlgorithm, this.sign_key);
        return builder.compact();
    }

    public Claims parseJWT(String token) {
        return Jwts.parser()
                .setSigningKey(this.sign_key)
                .parseClaimsJws(token)
                .getBody();
    }
}
