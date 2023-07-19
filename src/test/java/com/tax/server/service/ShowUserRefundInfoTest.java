package com.tax.server.service;

import com.tax.server.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowUserRefundInfoTest { // 회원 환급 정보 반환 관련 단위 테스트

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;


    @Test
    public void testShowUserRefundInfo_InvalidToken() { // 유효하지 않은 토큰으로 회원 환급 정보 반환 요청 시
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // 유효하지 않은 토큰 설정
        String invalidToken = "invalid.token.string";
        when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

        // 토큰 헤더에 추가
        request.addHeader("Authorization", "Bearer " + invalidToken);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> userService.showUserRefundInfo(request));
    }
}

