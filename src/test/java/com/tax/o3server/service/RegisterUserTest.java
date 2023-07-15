package com.tax.o3server.service;

import com.tax.o3server.dto.RegisterUserDTO;
import com.tax.o3server.entity.Users;
import com.tax.o3server.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RegisterUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUserIdNotBlank() { // @NotBlank validation
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setUserId(null);

        Set<String> violations = validate(dto);
        assertTrue(violations.contains("아이디를 기입해주세요."));
    }

    @Test
    public void testPasswordNotBlank() { // @NotBlank validation
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setPassword("");

        Set<String> violations = validate(dto);
        assertTrue(violations.contains("비밀번호를 기입해주세요."));
    }

    @Test
    public void testNameNotBlank() { // @NotBlank validation
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setName(" ");

        Set<String> violations = validate(dto);
        assertTrue(violations.contains("이름을 기입해주세요."));
    }

    @Test
    public void testRegNoNotBlank() { // @NotBlank validation
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setRegNo("");

        Set<String> violations = validate(dto);
        assertTrue(violations.contains("주민등록번호를 기입해주세요."));
    }

    // Helper method to validate the DTO
    private Set<String> validate(RegisterUserDTO dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(dto).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    @Test
    public void testRegisterUser_ValidUser() { // 회원가입 성공 요청
        // given
        RegisterUserDTO validUserDTO = new RegisterUserDTO();
        validUserDTO.setUserId("validUser");
        validUserDTO.setPassword("password123");
        validUserDTO.setName("홍길동");
        validUserDTO.setRegNo("860824-1655068");

        // Mock userRepository의 existsByUserId 메서드 반환값 설정
        when(userRepository.existsByUserId("validUser")).thenReturn(false);
        // Mock passwordEncoder의 encode 메서드 반환값 설정
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        // Mock userRepository의 save 메서드가 인자로 전달되면 저장되었다고 가정합니다.
        when(userRepository.save(any(Users.class))).thenReturn(null);

        // when
        userService.registerUser(validUserDTO);

        // then
        // userRepository의 existsByUserId 메서드가 한 번 호출되었는지 검증합니다.
        verify(userRepository, times(1)).existsByUserId("validUser");
        // passwordEncoder의 encode 메서드가 한 번 호출되었는지 검증합니다.
        verify(passwordEncoder, times(1)).encode("password123");
        // userRepository의 save 메서드가 한 번 호출되었는지 검증합니다.
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    public void testRegisterUser_ExistingUser() { // 존재하는 유저 정보로 회원가입을 시도
        // given
        RegisterUserDTO existingUserDTO = new RegisterUserDTO();
        existingUserDTO.setUserId("existingUser");
        existingUserDTO.setPassword("password456");
        existingUserDTO.setName("김둘리");
        existingUserDTO.setRegNo("921108-1582816");

        // Mock userRepository의 existsByUserId 메서드 반환값 설정
        when(userRepository.existsByUserId("existingUser")).thenReturn(true);

        // when & then
        // 이미 가입된 유저가 있을 경우 예외가 발생하는지 검증합니다.
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(existingUserDTO));
        // userRepository의 existsByUserId 메서드가 한 번 호출되었는지 검증합니다.
        verify(userRepository, times(1)).existsByUserId("existingUser");
        // passwordEncoder의 encode 메서드가 호출되지 않았는지 검증합니다.
        verify(passwordEncoder, never()).encode(anyString());
        // userRepository의 save 메서드가 호출되지 않았는지 검증합니다.
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    public void testRegisterUser_InvalidUser() { // 허용되지 않은 정보로 회원가입을 시도
        // given
        RegisterUserDTO invalidUserDTO = new RegisterUserDTO();
        invalidUserDTO.setUserId("invalidUser");
        invalidUserDTO.setPassword("password789");
        invalidUserDTO.setName("John Doe"); // 허용되지 않은 이름
        invalidUserDTO.setRegNo("123456-7890123"); // 허용되지 않은 주민등록번호

        // when & then
        // 허용되지 않은 이름과 주민등록번호를 사용하여 회원가입할 경우 예외가 발생하는지 검증합니다.
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(invalidUserDTO));
        // passwordEncoder의 encode 메서드가 호출되지 않았는지 검증합니다.
        verify(passwordEncoder, never()).encode(anyString());
        // userRepository의 save 메서드가 호출되지 않았는지 검증합니다.
        verify(userRepository, never()).save(any(Users.class));
    }
}
