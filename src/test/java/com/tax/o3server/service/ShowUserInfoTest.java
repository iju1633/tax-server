package com.tax.o3server.service;

import com.tax.o3server.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowUserInfoTest { // 회원 정보 반환 관련 단위 테스트

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Test
    public void testShowUserInfo_InvalidToken() { // 유효하지 않은 토큰으로 유저 정보 반환 요청 시
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // 유효하지 않은 토큰 생성
        String invalidToken = "invalid.token.string";

        // 토큰 검증 실패를 모의 설정
        when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

        // 헤더에 유효하지 않은 토큰 추가
        request.addHeader("Authorization", "Bearer " + invalidToken);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.showUserInfo(request);
        });
    }
}
