package org.unibl.etf.sni.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.unibl.etf.sni.model.User;

@Service
public class JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails, String remoteAddr, String userAgent) {
        return generateToken(new HashMap<>(), userDetails, remoteAddr, userAgent);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String remoteAddr, String userAgent) {
        if(userDetails instanceof User){
            User user = (User) userDetails;
            extraClaims.put("role", user.getRole().toString());
            extraClaims.put("ip", remoteAddr);
            extraClaims.put("userAgent", userAgent);
        }
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    public String extractUserRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extendToken(String expiredToken) {
        // jer je samo preglupo baca JwtExpiredException kada ga produzavam joj boze

        Claims claims;
        try {
            claims = getAllClaimsFromToken(expiredToken);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }

        Date newExpirationDate = new Date(System.currentTimeMillis() + (1800 * 1000)); // 30 minutes

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(newExpirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
