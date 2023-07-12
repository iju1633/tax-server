package com.tax.o3server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginSuccessDTO {

    @ApiModelProperty(value = "서비스 로그인 성공 시 반환되는 Jwt", example = "token example", required = true)
    private String token;
}
