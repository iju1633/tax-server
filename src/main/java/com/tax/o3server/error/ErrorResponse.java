package com.tax.o3server.error;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@Getter
@Setter
public class ErrorResponse { // controller 단에서 에러 발생 시 반환하는 에러 형식
    private String timeStamp; // 에러 일시
    private String code; // 에러 코드 (http status code)
    private String message; // 에러 메시지

    public ErrorResponse(String code, String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초"); // timeStamp 포맷
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        this.code = code;
        this.message = message;
        this.timeStamp = formatter.format(Calendar.getInstance().getTime());
    }
}
