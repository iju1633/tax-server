package com.tax.o3server.controller;

import com.tax.o3server.dto.*;
import com.tax.o3server.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 회원가입
    @PostMapping("/szs/signup")
    @ApiOperation(value = "회원 가입", notes = "회원가입을 진행합니다.\n모든 필드 값은 null 이 될 수 없습니다.\n 특정 유저만 회원가입이 가능합니다.")
    public ResponseEntity<Void> registerUser(@Validated @RequestBody RegisterUserDTO registerUserDTO) {

        userService.registerUser(registerUserDTO);
        return ResponseEntity.ok().build();
    }

    // 유저 로그인
    @PostMapping("/szs/login")
    @ApiOperation(value = "로그인", notes = "아이디와 비밀번호를 받아 로그인을 진행합니다.\n로그인 성공 시, 토큰을 반환합니다.")
    public ResponseEntity<LoginSuccessDTO> login(@RequestBody LoginDTO loginDTO) {

        return ResponseEntity.ok().body(userService.login(loginDTO));
    }

    // 유저 정보(아이디, 이름, 가입 날짜) 반환
    @GetMapping("/szs/me")
    @ApiOperation(value = "회원 정보 반환", notes = "회원의 서비스 로그인 아이디, 이름, 가입 날짜를 반환합니다.")
    public ResponseEntity<UserDTO> showUserInfo(HttpServletRequest httpServletRequest) {

        return ResponseEntity.ok().body(userService.showUserInfo(httpServletRequest));
    }

    // 유저 정보 스크랩
    @PostMapping("/szs/scrap")
    @ApiOperation(value = "유저 정보 스크랩", notes = "결정세액과 퇴직연금세액공제금액 계산에 필요한 정보를 DB에 저장합니다.")
    public ResponseEntity<Void> saveRefundInfo(HttpServletRequest httpServletRequest, @RequestBody RequestRefundDTO requestRefundDTO) {

        userService.saveRefundInfo(httpServletRequest, requestRefundDTO);
        return ResponseEntity.ok().build();
    }
}
