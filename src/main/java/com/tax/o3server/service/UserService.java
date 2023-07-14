package com.tax.o3server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.o3server.constant.RegisterConst;
import com.tax.o3server.constant.ScrapConst;
import com.tax.o3server.dto.*;
import com.tax.o3server.entity.ScrapData;
import com.tax.o3server.entity.Users;
import com.tax.o3server.repository.ScrapDataRepository;
import com.tax.o3server.repository.UserRepository;
import com.tax.o3server.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserService { // 유저 관련 서비스 로직 구현

    private final UserRepository userRepository;
    private final ScrapDataRepository scrapDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    @Value("${jwt.secret}")
    private String secret;

    public UserService(UserRepository userRepository, ScrapDataRepository scrapDataRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.scrapDataRepository = scrapDataRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // 유저 회원가입
    @Transactional
    public void registerUser(RegisterUserDTO registerUserDTO) {

        // 회원가입에 필요한 필드 세팅
        String name = registerUserDTO.getName();
        String regNo = registerUserDTO.getRegNo();

        // 유저 회원가입 가능 목록으로 회원가입 가능 여부 판단
        if (!RegisterConst.ALLOWED_USERS.containsKey(name) || !RegisterConst.ALLOWED_USERS.get(name).equals(regNo)) {
            throw new IllegalArgumentException("회원가입할 수 없는 정보입니다.");
        }

        // 이미 가입된 회원인 지 검증
        if (userRepository.existsByUserId(registerUserDTO.getUserId())) {
            throw new IllegalArgumentException("이미 가입된 유저입니다.");
        }

        // dto -> entity
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분");

        Users newUsers = new Users();
        newUsers.setUserId(registerUserDTO.getUserId()); // 서비스 로그인 아이디
        newUsers.setPassword(passwordEncoder.encode(registerUserDTO.getPassword())); // 민감정보 암호화 (비밀번호)
        newUsers.setName(name); // 유저 이름
        newUsers.setRegNo(passwordEncoder.encode(regNo)); // 민감정보 암호화 (주민번호)
        newUsers.setRegisterDate(formatter.format(LocalDateTime.now())); // 가입일자

        // db에 저장
        userRepository.save(newUsers);
    }

    // 로그인
    public LoginSuccessDTO login(LoginDTO loginDTO) {

        Users user = userRepository.findByUserId(loginDTO.getUserId());

        // 아이디 존재하지 않는 경우 or 비밀번호 불일치 시
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하셨습니다.");
        }

        // 사용자 인증 성공 시 JWT 토큰 생성
        String token = jwtUtils.generateToken(user);

        // 생성된 토큰을 담아 반환할 dto 생성
        LoginSuccessDTO loginSuccessDTO = new LoginSuccessDTO();
        loginSuccessDTO.setToken(token);

        // 토큰을 응답에 포함하여 반환
        return loginSuccessDTO;
    }

    // 유저 정보(아이디, 이름, 가입 날짜) 반환
    public UserDTO showUserInfo(HttpServletRequest httpServletRequest) {

        // 토큰 검증에 문제가 있는 경우
        if (!validateToken(httpServletRequest)) {
            throw new IllegalArgumentException("토큰 인증에 실패하셨습니다. 로그인 후, 다시 인증해주세요.");
        }

        // user 객체 찾아옴
        String token = httpServletRequest.getHeader("Authorization");
        Claims claims = decodeJwt(token, secret);
        Users user = userRepository.findByName(claims.getSubject()); // 이미 특정 정보로만 가입할 수 있기에 유일성이 확보됨

        // dto로 변환 후 반환
        return UserDTO.from(user);
    }

    // 토큰 유효한지 검증
    public boolean validateToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");

        if (token == null) {
            return false;
        }

        if (token.startsWith("Bearer ")) {
            String jwt = token.substring(7); // "Bearer " 접두어 제거
            // 토큰이 유효하지 않은 경우에 대한 처리
            return jwtUtils.validateToken(jwt);
        }

        return true;
    }

    // 토큰과 키값으로부터 유저이름, 토큰 생성 시간, 토큰 만료 시간을 갖는 claims 반환
    public Claims decodeJwt(String jwt, String secret) {

        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwt).getBody();
    }

    // 유저 정보 스크랩
    @Transactional
    public void saveRefundInfo(HttpServletRequest httpServletRequest, RequestRefundDTO requestRefundDTO) {

        // 토큰 검증에 문제가 있는 경우 (만료 예외)
        if (!validateToken(httpServletRequest)) {
            throw new IllegalArgumentException("토큰 인증에 실패하셨습니다. 로그인 후, 다시 인증해주세요.");
        }

        // jwt 토큰으로부터 요청자와 일치하는 지 검증
        String token = httpServletRequest.getHeader("Authorization");
        Claims claims = decodeJwt(token, secret);
        Users user = userRepository.findByName(claims.getSubject()); // 이미 특정 정보로만 가입할 수 있기에 유일성이 확보됨

        // 로그인한 회원과 요청하는 회원이 다른 경우에 대한 검증
        if (!user.getName().equals(requestRefundDTO.getName()) || !passwordEncoder.matches(requestRefundDTO.getRegNo(), user.getRegNo())) {
            throw new IllegalArgumentException("자신의 정보만 스크랩할 수 있습니다. 다시 시도해주세요.");
        }

        // JSON 데이터 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestRefundDTO);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 데이터 생성 중 오류가 발생했습니다.");
        }

        // POST 요청 설정
        String url = ScrapConst.HTTP_CLIENT_BASE_URL + "/scrap";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, token);
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

        // POST 요청 보내기
        HttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new IllegalArgumentException("요청 중 오류가 발생했습니다.");
        }

        // 응답 처리
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseBody;
                try {
                    responseBody = EntityUtils.toString(entity);
                } catch (IOException e) {
                    throw new IllegalArgumentException("응답 데이터를 읽는 중 오류가 발생했습니다.");
                }
                // JSON 응답 파싱
                try {
                    ObjectMapper responseMapper = new ObjectMapper();
                    ResponseData responseData = responseMapper.readValue(responseBody, ResponseData.class);

                    // status 값 확인
                    if ("success".equals(responseData.getStatus())) {

                        // 엔티티에 값을 저장하는 로직을 구현해주세요.
                        JsonList jsonList = responseData.getData().getJsonList();

                        long 산출세액 = Long.parseLong(jsonList.get산출세액().replaceAll(",", ""));
                        long 총급여 = Long.parseLong(jsonList.get급여().get(0).총지급액.replaceAll(",", ""));

                        List<SodukGongje> 소득공제 = jsonList.get소득공제();
                        long 보험료납입금액 = Long.parseLong(소득공제.get(0).get금액().replaceAll(",", ""));
                        long 교육비납입금액 = Long.parseLong(소득공제.get(1).get금액().replaceAll(",", ""));
                        long 기부금납입금액 = Long.parseLong(소득공제.get(2).get금액().replaceAll(",", ""));
                        long 의료비납입금액 = Long.parseLong(소득공제.get(3).get금액().replaceAll(",", ""));
                        long 퇴직연금납입금액 = Long.parseLong(소득공제.get(4).get총납임금액().replaceAll(",", ""));

                        // 엔티티 생성
                        ScrapData newScrapData = ScrapData.builder()
                                .총급여(총급여)
                                .산출세액(산출세액)
                                .퇴직연금납입금액(퇴직연금납입금액)
                                .보험료납입금액(보험료납입금액)
                                .의료비납입금액(의료비납입금액)
                                .교육비납입금액(교육비납입금액)
                                .기부금납입금액(기부금납입금액)
                                .build();

                        // 엔티티 저장
                        List<ScrapData> scrapDataList = scrapDataRepository.findAll();
                        if (scrapDataList.size() == 1) { // 기존에 스크랩 데이터가 저장되어 있는 경우, 갱신
                            ScrapData existingScrapData = scrapDataList.get(0);
                            existingScrapData.set총급여(총급여);
                            existingScrapData.set산출세액(산출세액);
                            existingScrapData.set퇴직연금납입금액(퇴직연금납입금액);
                            existingScrapData.set보험료납입금액(보험료납입금액);
                            existingScrapData.set의료비납입금액(의료비납입금액);
                            existingScrapData.set교육비납입금액(교육비납입금액);
                            existingScrapData.set기부금납입금액(기부금납입금액);

                            scrapDataRepository.save(existingScrapData);
                        } else if (scrapDataList.size() > 1) { // 스크랩 데이터가 2개 이상 있는 경우, 일괄 삭제 후 삽입
                            scrapDataRepository.deleteAll();
                            scrapDataRepository.save(newScrapData);
                        } else { // 데이터가 하나도 없는 경우, 삽입
                            scrapDataRepository.save(newScrapData);
                        }
                    } else {
                        throw new IllegalArgumentException("스크랩 도중 에러가 발생했습니다. 다시 시도해주세요.");
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException("응답 데이터 파싱 중 오류가 발생했습니다.", e);
                }
            }
        } else {
            throw new IllegalArgumentException("서버에서 오류 응답을 받았습니다. 다시 시도해주세요.");
        }
    }

    // 회원 환급 정보 반환
    public RefundDTO showUserRefundInfo(HttpServletRequest httpServletRequest) {

        // 토큰 검증에 문제가 있는 경우 (만료 예외)
        if (!validateToken(httpServletRequest)) {
            throw new IllegalArgumentException("토큰 인증에 실패하셨습니다. 로그인 후, 다시 인증해주세요.");
        }

        // jwt 토큰으로부터 요청자와 일치하는 지 검증
        String token = httpServletRequest.getHeader("Authorization");
        Claims claims = decodeJwt(token, secret);
        Users user = userRepository.findByName(claims.getSubject()); // 이미 특정 정보로만 가입할 수 있기에 유일성이 확보됨

        // 로그인한 사용자와 요청하는 사용자가 동일한지를 확인
        if (!user.getName().equals(claims.getSubject())) {
            throw new IllegalArgumentException("자신의 정보만 조회할 수 있습니다. 다시 시도해주세요.");
        }

        // 환급 정보가 없거나 오류로 2개 이상인 경우를 검증
        List<ScrapData> scrapDataList = scrapDataRepository.findAll();
        if (scrapDataList.size() != 1) {
            throw new IllegalArgumentException("환급 정보 반환 중 에러가 발생했습니다. 다시 시도해주세요.");
        }

        // 계산식에 필요한 필드값 불러오기
        ScrapData scrapData = scrapDataList.get(0);
        long 총급여 = scrapData.get총급여();
        long 산출세액 = scrapData.get산출세액();
        long 퇴직연금납입금액 = scrapData.get퇴직연금납입금액();
        long 보험료납입금액 = scrapData.get보험료납입금액();
        long 의료비납입금액 = scrapData.get의료비납입금액();
        long 교육비납입금액 = scrapData.get교육비납입금액();
        long 기부금납입금액 = scrapData.get기부금납입금액();

        // 계산식 적용
        // 결정세액 계산에 필요한 필드 값 계산 -> 결정세액 = 산출세액 - 근로소득세액공제금액 - 특별세액공제금액 - 표준세액공제금액 - 퇴직연금세액공제금액
        double 근로소득세액공제금액 = 산출세액 * 0.55;

        // 특별세액공제금액 계산
        double 보험료공제금액 = 보험료납입금액 * 0.12;
        double 의료비공제금액 = (의료비납입금액 - 총급여 * 0.03) * 0.15;
        if (의료비공제금액 < 0) {
            의료비공제금액 = 0;
        }
        double 교육비공제금액 = 교육비납입금액 * 0.15;
        double 기부금공제금액 = 기부금납입금액 * 0.15;
        double 특별세액공제금액 = 보험료공제금액 + 의료비공제금액 + 교육비공제금액 + 기부금공제금액;

        // 표준세액공제금액 계산
        double 표준세액공제금액;
        if (특별세액공제금액 < 130000) {
            표준세액공제금액 = 130000;
            특별세액공제금액 = 0;
        } else {
            표준세액공제금액 = 0;
        }

        // 결정세액 = 산출세액 - 근로소득세액공제금액 - 특별세액공제금액 - 표준세액공제금액 - 퇴직연금세액공제금액
        double 퇴직연급세액공제 = 퇴직연금납입금액 * 0.15;
        double 결정세액 = 산출세액 - 근로소득세액공제금액 - 특별세액공제금액 - 표준세액공제금액 - 퇴직연급세액공제;
        if (결정세액 < 0) {
            결정세액 = 0;
        }

        // 반환할 환급 정보를 dto에 세팅
        RefundDTO refundDTO = new RefundDTO();
        refundDTO.set이름(user.getName());
        refundDTO.set결정세액(formatNumber((long) 결정세액));
        refundDTO.set퇴직연금세액공제금액(formatNumber((long) 퇴직연급세액공제));

        // 환급 정보 반환
        return refundDTO;
    }

    // 로그아웃 (배포용)
    public void logout() {

        // Users 테이블 초기화
        userRepository.deleteAll();

        // ScrapData 테이블 초기화
        scrapDataRepository.deleteAll();
    }

    // 스크랩 시 필요한 클래스 정의
    @Getter
    @Setter
    static class ResponseData {
        private String status;
        private Data data;
        private Errors errors;
    }

    @Getter
    @Setter
    static class Data {
        private JsonList jsonList;
        private String appVer;
        private String errMsg;
        private String company;
        private String svcCd;
        private String hostNm;
        private String workerResDt;
        private String workerReqDt;
    }

    @Getter
    @Setter
    static class JsonList {
        private List<Gyupyeo> 급여;
        private String 산출세액;
        private List<SodukGongje> 소득공제;
    }

    @Getter
    @Setter
    static class Gyupyeo {
        private String 소득내역;
        private String 총지급액;
        private String 업무시작일;
        private String 기업명;
        private String 이름;
        private String 지급일;
        private String 업무종료일;
        private String 주민등록번호;
        private String 소득구분;
        private String 사업자등록번호;
    }

    @Getter
    @Setter
    static class SodukGongje {
        private String 금액;
        private String 소득구분;
        private String 총납임금액;
    }

    @Getter
    @Setter
    static class Errors {
    }

    // long 값에 세자리마다 ,(콤마)를 넣어 String 형식으로 반환
    public String formatNumber(long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }
}