package com.tax.o3server.dto;

import com.tax.o3server.entity.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class UserDTO {

    @ApiModelProperty(value = "서비스 로그인 아이디", example = "garfield", required = true)
    private String userId;

    @NotBlank(message = "이름을 기입해주세요.")
    @ApiModelProperty(value = "유저 이름", example = "홍길동", required = true)
    private String name;

    @ApiModelProperty(value = "가입일자", example = "2023.07.10")
    private String registerDate;

    public static UserDTO from(Users entity) {

        return UserDTO.builder()
                .userId(entity.getUserId())
                .name(entity.getName())
                .registerDate(entity.getRegisterDate())
                .build();
    }
}
