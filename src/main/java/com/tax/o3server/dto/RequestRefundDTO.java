package com.tax.o3server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RequestRefundDTO { // 스크랩 요청 시 사용

    @NotBlank(message = "이름을 기입해주세요.")
    @ApiModelProperty(value = "유저 이름", example = "홍길동", required = true)
    private String name;

    @NotBlank(message = "주민등록번호를 기입해주세요.")
    @ApiModelProperty(value = "주민등록번호", example = "860824-1655068", required = true)
    private String regNo;
}
