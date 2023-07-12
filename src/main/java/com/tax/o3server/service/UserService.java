package com.tax.o3server.service;

import com.tax.o3server.constant.RegisterConst;
import com.tax.o3server.dto.RegisterUserDTO;
import com.tax.o3server.entity.Users;
import com.tax.o3server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public List<Users> showUsers() {

        return userRepository.findAll();
    }
}
