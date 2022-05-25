package com.dex.coreserver.security;

import com.dex.coreserver.util.SecurityUtils;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    private static final long weekInMilliseconds = 7 * 24 * 60 * 1000;
    private String SECRET = SecurityUtils.getSecret();

    public String generate2faToken(long userId) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + weekInMilliseconds);

        return Jwts.builder().setSubject(Long.toString(userId))
                .setIssuedAt(now).setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    public boolean is2FaTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
