package com.tax.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginDTO { // 로그인 시 사용

    @NotBlank(message = "아이디를 기입해주세요.")
    @ApiModelProperty(value = "서비스 로그인 아이디", example = "garfield", required = true)
    private String userId;

    @NotBlank(message = "비밀번호를 기입해주세요.")
    @ApiModelProperty(value = "서비스 로그인 비밀번호", example = "1234", required = true)
    private String password;
}
