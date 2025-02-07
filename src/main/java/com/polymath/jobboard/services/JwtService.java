package com.polymath.jobboard.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessToken;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshToken;

    public String generateAccessToken(String subject){
        return generateToken(subject,accessToken);
    }

    public String generateRefreshToken(String subject){
        return generateToken(subject,refreshToken);
    }

    public String generateToken(String subject,long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expirationTime))
                .and()
                .signWith(getSecretKey()).compact();
    }
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private  <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);

    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    ...there is more to this
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
    public boolean isTokenExpired(String token) {
         LocalDateTime expirationDateTime = expirationDate(token);
         LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
         return expirationDateTime.isBefore(now);

    }
    public LocalDateTime expirationDate(String token){
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return convertDateToLocalDateTime(expirationDate);
    }
    private SecretKey getSecretKey(){
        byte[] encodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(encodedKey);
    }
    private LocalDateTime convertDateToLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
