package com.tax.o3server.service;

import com.tax.o3server.constant.RegisterConst;
import com.tax.o3server.dto.LoginDTO;
import com.tax.o3server.dto.LoginSuccessDTO;
import com.tax.o3server.dto.RegisterUserDTO;
import com.tax.o3server.dto.UserDTO;
import com.tax.o3server.entity.Users;
import com.tax.o3server.repository.UserRepository;
import com.tax.o3server.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    @Value("${jwt.secret}")
    private String secret;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // 유저 회원가입
    public void registerUser(RegisterUserDTO registerUserDTO) {

        if (userRepository.existsByUserId(registerUserDTO.getUserId())) {
            throw new IllegalArgumentException("이미 가입된 유저입니다.");
        }

        String name = registerUserDTO.getName();
        String regNo = registerUserDTO.getRegNo();

        // 유저 회원가입 가능 목록으로 회원가입 가능 여부 판단
        if (!RegisterConst.ALLOWED_USERS.containsKey(name) || !RegisterConst.ALLOWED_USERS.get(name).equals(regNo)) {
            throw new IllegalArgumentException("회원가입할 수 없는 정보입니다.");
        }

        // dto -> entity
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분");

        Users newUsers = new Users();
        newUsers.setUserId(registerUserDTO.getUserId()); // 서비스 로그인 아이디
        newUsers.setPassword(passwordEncoder.encode(registerUserDTO.getPassword())); // 민감정보 암호화 (비밀번호)
        newUsers.setName(name); // 유저 이름
        newUsers.setRegNo(passwordEncoder.encode(regNo)); // 민감정보 암호화 (주민번호)
        newUsers.setRegisterDate(formatter.format(LocalDateTime.now())); // 가입일자

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

    // 유저 정보(아이디, 이름, 가입 날짜) 반환
    public UserDTO showUserInfo(HttpServletRequest httpServletRequest) {

        // 토큰 검증에 문제가 있는 경우
        if (!validateToken(httpServletRequest)) {
            throw new IllegalArgumentException("토큰 인증에 실패하셨습니다. 로그인 후, 다시 인증해주세요.");
        }

        // user 객체 찾아옴
        String token = httpServletRequest.getHeader("Authorization");
        Claims claims = decodeJwt(token, secret);
        Users user = userRepository.findByName(claims.getSubject()); // 이미 특정 정보로만 가입할 수 있기에 유일성이 확보됨

        // dto로 변환 후 반환
        return UserDTO.from(user);
    }

    // 토큰 유효한지 검증
    public boolean validateToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");

        if (token == null) {
            return false;
        }

        if (token.startsWith("Bearer ")) {
            String jwt = token.substring(7); // "Bearer " 접두어 제거
            // 토큰이 유효하지 않은 경우에 대한 처리
            return jwtUtils.validateToken(jwt);
        }

        return true;
    }

    // 토큰과 키값으로부터 유저이름, 토큰 생성 시간, 토큰 만료 시간을 갖는 claims 반환
    public static Claims decodeJwt(String jwt, String secret) {

        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(jwt)
                .getBody();
    }
}
