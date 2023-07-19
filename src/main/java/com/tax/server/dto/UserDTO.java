package com.tax.server.dto;

import com.tax.server.entity.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO { // 유저 정보 반환 시 사용

    @ApiModelProperty(value = "서비스 로그인 아이디", example = "garfield")
    private String userId;

    @ApiModelProperty(value = "유저 이름", example = "홍길동")
    private String name;

    @ApiModelProperty(value = "가입일자", example = "2023.07.10")
    private String registerDate;

    public static UserDTO from(Users entity) { // entity -> dto

        return UserDTO.builder()
                .userId(entity.getUserId())
                .name(entity.getName())
                .registerDate(entity.getRegisterDate())
                .build();
    }
}
