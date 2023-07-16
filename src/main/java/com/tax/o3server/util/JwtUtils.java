package com.tax.o3server.util;

import com.tax.o3server.constant.JwtUtilConst;
import com.tax.o3server.entity.Users;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils { // jwt 사용에 필요한 util

    public String generateToken(Users user) { // jwt 토큰 생성
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JwtUtilConst.EXPIRATION_DURATION);

        return Jwts.builder()
                .setSubject(user.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, JwtUtilConst.SECRET.getBytes())
                .compact();
    }

    public boolean validateToken(String token) { // jwt 토큰 검증
        try {
            Jwts.parser().setSigningKey(JwtUtilConst.SECRET.getBytes()).parseClaimsJws(token.trim().replaceAll("\uFFFD", ""));
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
