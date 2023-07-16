package com.tax.o3server.service;

import com.tax.o3server.entity.Users;
import com.tax.o3server.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest { // JWT 토큰 관련 단위 테스트

    @Mock
    private JwtUtils jwtUtils;

    @Test
    public void testGenerateToken() { // 토큰 생성
        // given
        Users user = new Users();
        user.setName("홍길동");

        // when
        JwtUtils jwtUtils = new JwtUtils();
        String token = jwtUtils.generateToken(user);

        // then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testValidateToken_ValidToken_ReturnsTrue() { // 토큰 유효 검사 (정상)
        // given
        Users user = new Users();
        user.setName("홍길동");

        // when
        JwtUtils jwtUtils = new JwtUtils();
        String validToken = jwtUtils.generateToken(user);
        boolean isValid = jwtUtils.validateToken(validToken);

        // then
        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_InvalidToken_ReturnsFalse() { // 토큰 유효 검사 (비정상)
        // given
        String invalidToken = "invalid.token.string"; // 비정상적인 토큰

        // when
        boolean isValid = jwtUtils.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }
}
