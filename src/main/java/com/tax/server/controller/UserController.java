package com.tax.server.controller;

import com.tax.server.dto.*;
import com.tax.server.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController { // rest api 정의

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 회원가입
    @PostMapping("/szs/signup")
    @ApiOperation(value = "회원 가입", notes = "회원가입을 진행합니다.\n모든 필드 값은 null 이 될 수 없습니다.\n 특정 정보로만 회원가입이 가능합니다.")
    public ResponseEntity<Void> registerUser(@Validated @RequestBody RegisterUserDTO registerUserDTO) {

        userService.registerUser(registerUserDTO);
        return ResponseEntity.ok().build();
    }

    // 회원 로그인
    @PostMapping("/szs/login")
    @ApiOperation(value = "로그인", notes = "아이디와 비밀번호를 받아 로그인을 진행합니다.\n로그인 성공 시, 토큰을 반환합니다.")
    public ResponseEntity<LoginSuccessDTO> login(@RequestBody LoginDTO loginDTO) {

        return ResponseEntity.ok().body(userService.login(loginDTO));
    }

    // 회원 정보(아이디, 이름, 가입 날짜) 반환
    @GetMapping("/szs/me")
    @ApiOperation(value = "회원 정보 반환", notes = "회원의 서비스 로그인 아이디, 이름, 가입 날짜를 반환합니다.")
    public ResponseEntity<UserDTO> showUserInfo(HttpServletRequest httpServletRequest) {

        return ResponseEntity.ok().body(userService.showUserInfo(httpServletRequest));
    }

    // 회원 정보 스크랩
    @PostMapping("/szs/scrap")
    @ApiOperation(value = "회원 정보 스크랩", notes = "결정세액과 퇴직연금세액공제금액을 계산하는데 필요한 값을 DB에 저장합니다.")
    public ResponseEntity<Void> saveRefundInfo(HttpServletRequest httpServletRequest, @RequestBody RequestRefundDTO requestRefundDTO) {

        userService.saveRefundInfo(httpServletRequest, requestRefundDTO);
        return ResponseEntity.ok().build();
    }

    // 회원 환급 정보 반환
    @GetMapping("/szs/refund")
    @ApiOperation(value = "회원 환급 정보 반환", notes = "계산식을 적용하여 결정세액과 퇴직연금세액공제금액을 계산한 후, 회원의 이름과 함께 이를 반환합니다.")
    public ResponseEntity<RefundDTO> showUserRefundInfo(HttpServletRequest httpServletRequest) {

        return ResponseEntity.ok().body(userService.showUserRefundInfo(httpServletRequest));
    }

    // 로그아웃 (배포용)
    @PostMapping("/szs/logout")
    @ApiOperation(value = "로그아웃 (배포용)", notes = "회원 정보가 담긴 테이블과 스크랩 데이터가 담긴 테이블을 비웁니다.\n웹 애플리케이션 서버를 종료 이후 다시 시작하는 효과를 주기 위합입니다.")
    public ResponseEntity<Void> logout() {

        userService.logout();
        return ResponseEntity.ok().build();
    }
}
