package com.tax.o3server.service;

import com.tax.o3server.dto.LoginDTO;
import com.tax.o3server.dto.LoginSuccessDTO;
import com.tax.o3server.entity.Users;
import com.tax.o3server.repository.UserRepository;
import com.tax.o3server.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginTest { // 로그인 관련 단위 테스트

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUserIdNotBlank() { // 로그인 시, 아이디 빈칸
        LoginDTO dto = new LoginDTO();
        dto.setUserId(null);

        Set<String> violations = validate(dto);
        assertTrue(violations.contains("아이디를 기입해주세요."));
    }

    @Test
    public void testPasswordNotBlank() { // 로그인 시, 비밀번호 빈칸
        LoginDTO dto = new LoginDTO();
        dto.setPassword("");

        Set<String> violations = validate(dto);
        assertTrue(violations.contains("비밀번호를 기입해주세요."));
    }

    private Set<String> validate(LoginDTO dto) { // @NotBlank 검증
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(dto).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    @Test
    public void testLogin_Success() { // 로그인 성공 요청
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserId("existingUser");
        loginDTO.setPassword("password123");

        Users existingUser = new Users();
        existingUser.setUserId("existingUser");
        existingUser.setPassword("encodedPassword");

        // Mock userRepository의 findByUserId 메서드 반환값 설정
        when(userRepository.findByUserId("existingUser")).thenReturn(existingUser);
        // Mock passwordEncoder의 matches 메서드 반환값 설정
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        // Mock jwtUtils의 generateToken 메서드 반환값 설정
        when(jwtUtils.generateToken(existingUser)).thenReturn("generatedToken");

        // when
        LoginSuccessDTO response = userService.login(loginDTO);

        // then
        assertNotNull(response); // 토큰이 생성되었는지 확인

        // userRepository의 findByUserId 메서드가 한 번 호출되었는지 검증합니다.
        verify(userRepository, times(1)).findByUserId("existingUser");
        // passwordEncoder의 matches 메서드가 한 번 호출되었는지 검증합니다.
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        // jwtUtils의 generateToken 메서드가 한 번 호출되었는지 검증합니다.
        verify(jwtUtils, times(1)).generateToken(existingUser);
    }

    @Test
    public void testLogin_InvalidUser() { // 존재하지 않는 정보로 로그인 요청 시
        // given
        LoginDTO invalidUserDTO = new LoginDTO();
        invalidUserDTO.setUserId("nonExistingUser");
        invalidUserDTO.setPassword("wrongPassword");

        // Mock userRepository의 findByUserId 메서드 반환값 설정
        when(userRepository.findByUserId("nonExistingUser")).thenReturn(null);

        // when & then
        // 아이디가 존재하지 않는 경우 예외가 발생하는지 검증합니다.
        assertThrows(IllegalArgumentException.class, () -> userService.login(invalidUserDTO));

        // userRepository의 findByUserId 메서드가 한 번 호출되었는지 검증합니다.
        verify(userRepository, times(1)).findByUserId("nonExistingUser");
        // passwordEncoder의 matches 메서드가 호출되지 않았는지 검증합니다.
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        // jwtUtils의 generateToken 메서드가 호출되지 않았는지 검증합니다.
        verify(jwtUtils, never()).generateToken(any(Users.class));
    }

    @Test
    public void testLogin_InvalidPassword() { // 회원가입 시 기입했던 비밀번호와 다른 비밀번호로 로그인 요청 시
        // given
        LoginDTO invalidPasswordDTO = new LoginDTO();
        invalidPasswordDTO.setUserId("existingUser");
        invalidPasswordDTO.setPassword("wrongPassword");

        Users existingUser = new Users();
        existingUser.setUserId("existingUser");
        existingUser.setPassword("encodedPassword");

        // Mock userRepository의 findByUserId 메서드 반환값 설정
        when(userRepository.findByUserId("existingUser")).thenReturn(existingUser);
        // Mock passwordEncoder의 matches 메서드 반환값 설정
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when & then
        // 비밀번호가 일치하지 않는 경우 예외가 발생하는지 검증합니다.
        assertThrows(IllegalArgumentException.class, () -> userService.login(invalidPasswordDTO));

        // userRepository의 findByUserId 메서드가 한 번 호출되었는지 검증합니다.
        verify(userRepository, times(1)).findByUserId("existingUser");
        // passwordEncoder의 matches 메서드가 한 번 호출되었는지 검증합니다.
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        // jwtUtils의 generateToken 메서드가 호출되지 않았는지 검증합니다.
        verify(jwtUtils, never()).generateToken(any(Users.class));
    }
}
