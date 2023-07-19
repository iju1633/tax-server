package com.tax.server.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundDTO { // 환급 정보 반환 시 사용

    @ApiModelProperty(value = "유저 이름", example = "홍길동")
    private String 이름;

    @ApiModelProperty(value = "결정세액", example = "150,000")
    private String 결정세액;

    @ApiModelProperty(value = "퇴직연금세액공제금액", example = "75,000")
    private String 퇴직연금세액공제금액;
}
