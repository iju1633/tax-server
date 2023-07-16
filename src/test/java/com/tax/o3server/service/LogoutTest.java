package com.tax.o3server.service;

import com.tax.o3server.repository.ScrapDataRepository;
import com.tax.o3server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutTest { // 로그아웃 (배포용) 관련 단위 테스트

    @InjectMocks
    private UserService userService;

    @Mock(lenient = true) // 불필요한 stubbing 방지 : 실제로 모킹된 메서드가 특정 값을 반환하거나 특정 동작을 수행하도록 설정하는 것 방지
    private UserRepository userRepository;

    @Mock(lenient = true) // 불필요한 stubbing 방지 : 실제로 모킹된 메서드가 특정 값을 반환하거나 특정 동작을 수행하도록 설정하는 것 방지
    private ScrapDataRepository scrapDataRepository;

    @Test
    public void testLogout() { // 로그아웃 (배포용) 요청 시
        // given
        // userRepository와 scrapDataRepository에서 데이터를 리턴하지 않도록 설정 (데이터 삭제 효과)
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(scrapDataRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        userService.logout();

        // then
        // userRepository와 scrapDataRepository의 deleteAll 메서드가 호출되었는지 검증
        verify(userRepository, times(1)).deleteAll();
        verify(scrapDataRepository, times(1)).deleteAll();
    }
}

