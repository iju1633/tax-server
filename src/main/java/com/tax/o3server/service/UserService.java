package com.tax.o3server.service;

import com.tax.o3server.constant.RegisterConst;
import com.tax.o3server.dto.LoginDTO;
import com.tax.o3server.dto.LoginSuccessDTO;
import com.tax.o3server.dto.RegisterUserDTO;
import com.tax.o3server.entity.Users;
import com.tax.o3server.repository.UserRepository;
import com.tax.o3server.util.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // 유저 회원가입
    public void registerUser(RegisterUserDTO registerUserDTO) {

        String name = registerUserDTO.getName();
        String regNo = registerUserDTO.getRegNo();

        // 유저 회원가입 가능 목록으로 회원가입 가능 여부 판단
        if (!RegisterConst.ALLOWED_USERS.containsKey(name) || !RegisterConst.ALLOWED_USERS.get(name).equals(regNo)) {
            throw new IllegalArgumentException("회원가입할 수 없는 정보입니다.");
        }

        // dto -> entity
        Users newUsers = new Users();
        newUsers.setUserId(registerUserDTO.getUserId());
        newUsers.setPassword(passwordEncoder.encode(registerUserDTO.getPassword())); // 민감정보 암호화
        newUsers.setName(name);
        newUsers.setRegNo(passwordEncoder.encode(regNo)); // 민감정보 암호화

        // db에 저장
        userRepository.save(newUsers);
    }

    // 로그인
    public LoginSuccessDTO login(LoginDTO loginDTO) {

        Users user = userRepository.findByUserId(loginDTO.getUserId());

        // 아이디 존재하지 않는 경우 or 비밀번호 불일치 시
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
        }

        // 사용자 인증 성공 시 JWT 토큰 생성
        String token = jwtUtils.generateToken(user);

        // 생성된 토큰을 담아 반환할 dto 생성
        LoginSuccessDTO loginSuccessDTO = new LoginSuccessDTO();
        loginSuccessDTO.setToken(token);

        // 토큰을 응답에 포함하여 반환
        return loginSuccessDTO;
    }
}
