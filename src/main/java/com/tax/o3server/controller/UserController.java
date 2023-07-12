package com.tax.o3server.controller;

import com.tax.o3server.dto.RegisterUserDTO;
import com.tax.o3server.entity.Users;
import com.tax.o3server.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    // 유저 반환
    @GetMapping("/szs/all")
    @ApiOperation(value = "회원 가입", notes = "회원가입을 진행합니다.\n모든 필드 값은 null 이 될 수 없습니다.\n 특정 유저만 회원가입이 가능합니다.")
    public ResponseEntity<List<Users>> showUsers() {

        return ResponseEntity.ok().body(userService.showUsers());
    }
}