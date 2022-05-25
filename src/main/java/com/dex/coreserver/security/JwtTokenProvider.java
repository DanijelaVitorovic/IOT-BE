package com.dex.coreserver.security;

import com.dex.coreserver.model.User;
import com.dex.coreserver.payload.JWTLoginSuccessResponse;
import com.dex.coreserver.payload.RefreshTokenRequest;
import com.dex.coreserver.service.UserService;
import com.dex.coreserver.util.SecurityUtils;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtTokenProvider {
    
    @Autowired
    private UserService userService;

    private String SECRET = SecurityUtils.getSecret();
    private String TOKEN_PREFIX = SecurityUtils.getTokenPrefix();
    private Long EXPIRATION_TIME = SecurityUtils.getAccessTokenExptime();
    private Long EXPIRATION_TIME_REFRESH_TOKEN = SecurityUtils.getRefreshTokenExpTime();

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, EXPIRATION_TIME_REFRESH_TOKEN);
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, EXPIRATION_TIME);
    }

    private String generateToken(Authentication authentication, long expirationTime) {

        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());

        Date expiryDate = new Date(now.getTime() + expirationTime);

        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", user.getUsername());
        claims.put("google2fa", user.getUseGoogle2f());
        claims.put("role",userService.findTopPriorityRoleByUser(user));

        return Jwts.builder().setSubject(userId).setClaims(claims)
                .setIssuedAt(now).setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    public JWTLoginSuccessResponse refreshToken(RefreshTokenRequest request) {
        JWTLoginSuccessResponse response = new JWTLoginSuccessResponse();
        response.setSuccess(false);
        response.setRefreshToken("");
        response.setToken("");

        if (!validateToken(request.getRefreshToken())) {
            return response;
        }

        String plainAccessToken = extractToken(request.getToken());
        if (!validateToken(plainAccessToken)) {
            return response;
        }

        String extendedAccessToken = extendToken(plainAccessToken, EXPIRATION_TIME);
        String accessTokenWithPrefix = packageAccessToken(extendedAccessToken);

        response.setToken(accessTokenWithPrefix);
        response.setRefreshToken(extendToken(request.getRefreshToken(), EXPIRATION_TIME_REFRESH_TOKEN));

        response.setSuccess(true);
        return response;
    }

    private String extractToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length(), bearerToken.length());
        }
        return null;
    }

    private String packageAccessToken(String token) {
        return TOKEN_PREFIX + token;
    }

    private String extendToken(String jwt, long time) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + time);
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwt).getBody();

        return Jwts.builder().setSubject(claims.getSubject()).setClaims(claims)
                .setIssuedAt(now).setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalit JWT Signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalit JWT Token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT Token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT Token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        String id = (String) claims.get("id");
        return Long.parseLong(id);
    }

    public Map<String, Long> getTokenIntervals(){
        Map<String, Long> response = new HashMap<>(  );
        response.put( "startRefreshInterval", SecurityUtils.getStartRefreshTokenInterval() );
        response.put( "checkTokenExpInterval", SecurityUtils.getCheckTokenExpInterval() );
        return response;
    }

}

