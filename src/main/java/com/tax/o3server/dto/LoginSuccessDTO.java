package com.tax.o3server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginSuccessDTO { // 로그인 성공 후 토큰 반환 시 사용

    @ApiModelProperty(value = "서비스 로그인 성공 시 반환되는 JWT", example = "token value example")
    private String token;
}
