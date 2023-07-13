package com.tax.o3server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RegisterUserDTO { // 회원가입 시 사용

    @NotBlank(message = "아이디를 기입해주세요.")
    @ApiModelProperty(value = "서비스 로그인 아이디", example = "garfield", required = true)
    private String userId;

    @NotBlank(message = "비밀번호 기입해주세요.")
    @ApiModelProperty(value = "서비스 로그인 비밀번호", example = "1234", required = true)
    private String password;

    @NotBlank(message = "이름을 기입해주세요.")
    @ApiModelProperty(value = "유저 이름", example = "홍길동", required = true)
    private String name;

    @NotBlank(message = "주민번호를 기입해주세요.")
    @ApiModelProperty(value = "주민번호", example = "860824-1655068", required = true)
    private String regNo;
}
