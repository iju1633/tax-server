package com.tax.o3server.util;

import com.tax.o3server.entity.Users;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils { // jwt 사용에 필요한 util
    @Value("${jwt.secret}")
    private String secret; // yml 파일에 정의한 key

    @Value("${jwt.expiration}")
    private long expiration; // yml 파일에 정의한 토큰 유효 시간

    public String generateToken(Users user) { // jwt 토큰 생성
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public boolean validateToken(String token) { // jwt 토큰 검증
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token.trim().replaceAll("\uFFFD", ""));
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
