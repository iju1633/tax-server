# 👕 2023, 세금 환급 정산 서비스

[Swagger Link](https://tax-server.kro.kr/swagger-ui/#/)  
회원의 정보를 스크랩하여 환급 정보를 반환하는 서비스를 제공합니다.

## Table of Contents
- [💪 Skill Stack](#-skill-stack)
- [🛠️ Tool](#️-tool)
- [📖 구현한 API](#-구현한-api)
- [📝 Documentation (#Issue)](#-documentation-issue)
- [📝 구현 방법 & 검증 결과](#-구현-방법--검증-결과)
- [🌐 Swagger 주소](#-swagger-주소)
- [🔙 Requirements / Solution](#-requirements--solution)
- [💎 Main Features](#-main-features)
- [📐 Service Architecture](#-service-architecture)
- [🖥️ Build Method](#%EF%B8%8F-build-method)
- [🗄️ ERD](#%EF%B8%8F-erd)
- [📃 API Documentation](#-api-documentation)
- [🏛️ Depedency Used](#%EF%B8%8F-depedency-used)

## 💪 Skill Stack
![Java](https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=JAVA&logoColor=white)
![Spring-Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white)
![H2](https://img.shields.io/badge/H2-4479A1.svg?&style=for-the-badge&logo=H2&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-0DB7ED?style=for-the-badge&logo=Docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-66E851?style=for-the-badge&logo=Swagger&logoColor=white)
![EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?style=for-the-badge&logo=Amazon-EC2&logoColor=white)
![Linux](https://img.shields.io/badge/linux-FCC624?style=for-the-badge&logo=linux&logoColor=black)
![GitHub_Actions](https://img.shields.io/badge/GitHub_Actions-1678D2?style=for-the-badge&logo=GithubActions&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-65AC6C?style=for-the-badge&logo=nginx&logoColor=white)

## 🛠️ Tool
![Git](https://img.shields.io/badge/Git-F05032.svg?&style=for-the-badge&logo=Git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-000000.svg?&style=for-the-badge&logo=Github&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/ItelliJ%20IDEA-4A93D7.svg?&style=for-the-badge&logo=intellij-idea&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000.svg?&style=for-the-badge&logo=Notion&logoColor=white)
![Draw.io](https://img.shields.io/badge/Draw.io-FF9900.svg?&style=for-the-badge&logo=Draw-io&logoColor=white)
![FileZilla](https://img.shields.io/badge/FileZilla-000000.svg?&style=for-the-badge&logo=FileZilla&logoColor=white)
![Let's Encrypt](https://img.shields.io/badge/Let's_Encrypt-452B67.svg?&style=for-the-badge&logo=Let's_Encrypt&logoColor=white)

## 📖 구현한 API
- 회원 가입
  - DB에 회원 정보 저장 
- 로그인
  - 성공 시, JWT 토큰 반환
  - 일부러 JWT 토큰의 유효 기간을 1분으로 두어 토큰의 유효기간 만료 여부를 검증하기 편하도록 설정
- 회원 정보 반환
  - 이름, 서비스 로그인 아이디, 그리고 가입 일시를 반환 
- 유저의 정보 스크랩
  - 외부 API 호출 이후 응답받은 JSON 데이터를 parsing 하여 결정세액과 퇴직연금세액공제금액 계산에 필요한 값을 DB에 저장
- 유저의 환급 정보 반환
  - 유저의 스크랩 정보를 바탕으로 결정세액과 퇴직연금세액공제금액을 계산하여 이름과 함께 반환
- 로그아웃 (배포용)
  - 배포된 상태에선 웹 애플리케이션 서버가 계속 돌아가며, docker container를 내리고 다시 실행시키기 위해선 key 값이 필요하기 때문에 웹 애플리케이션 서버를 종료시키고 다시 실행시킨 효과를 주기 위해서 구현
  - Embeded DB에 저장되어 있는 회원 정보와 스크랩 데이터를 삭제 

## 📝 Documentation (#Issue)
- [프로젝트 구현 조건](https://github.com/iju1633/3o3-server/issues/3)
- [브랜치 & 코드 & PR 관리 전략](https://github.com/iju1633/3o3-server/issues/1)
- [에러 핸들링](https://github.com/iju1633/3o3-server/issues/2)
- [Amazon Web Service 현황](https://github.com/iju1633/3o3-server/issues/29)
- [Log 관리](https://github.com/iju1633/3o3-server/issues/32)

## 📝 구현 방법 & 검증 결과
- 회원가입
  - 서비스 로직
    - 회원가입에 필요한 정보(서비스 로그인 아이디, 서비스 로그인 비밀번호, 이름, 주민등록번호)가 담긴 DTO를 파라미터로 받아 엔티티 생성하여 DB에 저장
  - 특징
    - 서비스 로그인 비밀번호와 주민등록번호의 경우, `PasswordEncoder`를 사용하여 암호화 진행
    - `JpaRepository`를 상속받은 인터페이스를 활용하여 DB에 저장
  - 예외 상황
    - 파라미터로 받은 DTO에 하나의 속성이라도 null 값이 있는 경우 
    - 요구 사항에 적힌 유저 회원가입 가능 정보와 일치하지 않는 경우
    - 이미 회원가입된 정보로 회원가입을 시도하는 경우
  - 검증 결과 (요청 성공)
    - 코드 200 반환 
     <img width="1193" alt="스크린샷 2023-07-15 오전 9 52 33" src="https://github.com/iju1633/3o3-server/assets/43805087/e545044a-84da-474f-a71b-c5211852db51">
- 로그인
  - 서비스 로직
    - 로그인에 필요한 정보(서비스 로그인 아이디, 서비스 로그인 비밀번호)가 담긴 DTO를 파라미터로 받아 사용자 인증 후, JWT 토큰을 생성하고 이를 DTO에 담아 반환
  - 특징
    - 서비스 로그인 아이디/비밀번호 모두 일치해야 로그인 가능
    - 파라미터로 받은 DTO로 DB에 저장된 유저를 찾고 `PasswordEncoder`를 사용하여 암호화된 정보와 파라미터로 받은 정보(서비스 로그인 비밀번호)가 일치하는 지 확인
    - 로그인 성공 시, 해당 유저를 활용하여 JWT 토큰 생성(`JwtUtils.java`의 `generateToken()`활용)
    - 토큰을 응답 DTO에 포함하여 반환
  - 예외 상황
    - 파라미터로 받은 DTO에 하나의 속성이라도 null 값이 있는 경우 
    - 파라미터로 받은 정보로 유저 조회가 안되는 경우
    - 계정 정보 불일치 시
  - 검증 결과 (요청 성공)
    - 코드 200 반환하며 토큰 반환
      <img width="1198" alt="스크린샷 2023-07-15 오전 9 54 46" src="https://github.com/iju1633/3o3-server/assets/43805087/435c0405-b71a-457c-bf57-7b0530a4d6d4">
- 회원 정보 반환
  - 서비스 로직
    - 로그인 성공 시 반환된 JWT 토큰을 헤더에 넣어 요청하면 토큰과 키값으로부터 유저이름, 토큰 생성 시간, 토큰 만료 시간을 갖는 `Claims`를 사용하여 유저 객체를 DB에서 가져와 DTO로 변환 후 반환 
  - 특징
    - `HttpServletRequest`를 활용하여 헤더의 JWT 토큰을 가져옴
    - DTO class에 엔티티를 매개변수로 받아 DTO를 반환하는 `static`으로 선언한 메서드를 구현
    - 특정 정보로만 가입할 수 있기에 유저 이름으로 DB에서 유저를 찾아도 유일성이 확보됨
    - `JpaRepository`를 상속받은 인터페이스를 활용하여 DB에 저장
  - 예외 상황
    - 토큰이 형식이 이상한 경우
    - 토큰의 유효 기간이 지난 경우
  - 검증 결과 (요청 성공)
    - 코드 200 반환하며 서비스 로그인 아이디, 이름, 가입 일시를 반환
      <img width="1194" alt="스크린샷 2023-07-15 오전 9 56 40" src="https://github.com/iju1633/3o3-server/assets/43805087/1309e818-b421-4399-9a03-e27ebdbe0310">
- 유저의 정보 스크랩
  - 서비스 로직  
    <img width="660" alt="스크린샷 2023-07-04 오후 2 59 06" src="https://github.com/iju1633/3o3-server/assets/43805087/a4d4dffe-89fc-4343-a8a5-84ce97340e90" width="400" height="800">  
    - 스크랩 시 필요한 정보(이름, 주민등록번호)가 담긴 DTO와 로그인 성공 시 반환된 JWT 토큰을 헤더에 넣어 요청하면 토큰과 키값으로부터 유저이름, 토큰 생성 시간, 토큰 만료 시간을 갖는 `Claims`를 사용하여 사용자 인증 후, 파라미터로 받은 DTO로 외부 API 요청 시 필요한 JSON 형식의 데이터 생성 이후, `HttpClient`를 활용하여 POST 요청 후, 응답받은 데이터 중 `산출세액`, `총급여`, `보험료납입금액`, `교육비납입금액`, `기부금납입금액`, `의료비납입금액`, `퇴직연금납입금액` 값을 parsing하여 엔티티에 담아 DB에 저장
  - 특징
    - 외부 API의 base url은 상수로 관리
    - 외부 API 요청 후, 반환될 JSON 데이터를 저장할 class 정의
    - 외부 API 요청 후, `statusCode`가 200이며 `status` 필드 값이 `"success"`이면 parse 진행
    - parse 해온 값은 추후 계산에 용이하도록 `,`는 제거하고 `long` 값으로 변환 후 저장
    - 기존에 스크랩 데이터가 저장되어 있는 경우, 갱신
    - 스크랩 데이터가 2개 이상 있는 경우, 일괄 삭제 후 스크랩한 데이터 삽입
    - `JpaRepository`를 상속받은 인터페이스를 활용하여 DB에 저장
  - 예외 상황
    - 파라미터로 받은 DTO에 하나의 속성이라도 null 값이 있는 경우
    - 토큰이 형식이 이상한 경우
    - 토큰의 유효 기간이 지난 경우
    - 로그인한 회원과 요청하는 회원이 다른 경우
    - 외부 API 요청에 필요한 JSON 데이터 생성(`objectMapper.writeValueAsString`)중 오류가 나는 경우
    - 외부 API 요청을 하며 서비스 코드에서 `httpClient.execute()` 실행 시 `IOException`이 발생하는 경우
    - 외부 API 요청 이후 응답 데이터를 읽는 중 오류가 발생하는 경우
    - 외부 API 요청 이후 응답 데이터의 status 필드 값이 `"fail"`인 경우
    - 외부 API 요청 이후 응답 데이터가 없어 `IOException`이 발생하는 경우
    - 외부 API 요청 후, `statusCode`가 200이 아닌 경우
  - 검증 결과 (요청 성공)
    - 코드 200 반환
      <img width="1196" alt="스크린샷 2023-07-15 오전 9 58 36" src="https://github.com/iju1633/3o3-server/assets/43805087/afc83456-b655-4930-9fba-f2caf451c2a8">
- 유저의 환급 정보 반환
  - 서비스 로직
    - 로그인 성공 시 반환된 JWT 토큰을 헤더에 넣어 요청하면 토큰과 키값으로부터 유저이름, 토큰 생성 시간, 토큰 만료 시간을 갖는 `Claims`를 사용하여 사용자 검증을 거친 후 스크랩 시 DB에 저장한 필드 값을 가져와 요구 사항에 명시된 계산식을 활용하여 계산 이후 반환할 환급 정보(이름, 결정세액, 퇴직연금세액공제금액)를 DTO에 담아 반환
  - 특징
    - 요구 사항에 적힌 계산식과 참고 사항을 반영하여 필드 값 계산
    - 계산식 적용 중 소수점이 나올 수 있기에 `double` 형식을 사용하여 `근로소득세액공제금액`, `특별세액공제금액`, `표준세액공제금액`, `퇴직연급세액공제`, `결정세액` 계산
    - 환급 정보가 담긴 DTO 반환 시, `결정세액`과 `퇴직연급세액공제`은 `long`값으로 변환하여 소수점을 버린 후, 1000단위 별로 `,`를 붙여 반환
    - 환급 정보는 DTO에 담아 반환
  - 예외 상황
    - 토큰이 형식이 이상한 경우
    - 토큰의 유효 기간이 지난 경우
    - 로그인한 사용자와 요청하는 사용자가 다른 경우
    - 스크랩한 환급 정보가 없거나 오류로 인해 스크랩 데이터 엔티티에 담긴 row가 2개 이상인 경우
  - 검증 결과 (요청 성공)
    - 코드 200 반환하며 이름과 결정세액과 퇴직연금세액공제금액 반환
      <img width="1192" alt="스크린샷 2023-07-15 오전 9 59 31" src="https://github.com/iju1633/3o3-server/assets/43805087/c92b234f-9cde-42c7-9d2b-1f2159d06f35">
- 로그아웃
  - 서비스 로직
    - DB에 저장되어 있는 회원 정보와 스크랩 데이터를 물리적 삭제
  - 특징
    - 웹 애플리케이션 서버를 종료 이후 다시 실행시킨 효과를 주기 위해서 구현
  - 검증 결과 (요청 성공)
    - 코드 200 반환
      <img width="1194" alt="스크린샷 2023-07-15 오전 10 00 40" src="https://github.com/iju1633/3o3-server/assets/43805087/8d1cc1ca-1568-40e3-ac6d-ad504c5fb0ca">
- 요청에 대한 검증 실패의 경우
  - [에러 핸들링](https://github.com/iju1633/3o3-server/issues/2)의 `형식` 및 `에러나는 경우에 대한 에러메시지 참고`

## 🌐 Swagger 주소
- [Swagger Link](https://tax-server.kro.kr/swagger-ui/#/)
* 로컬에서 빌드하고 실행시키고자 한다면, [🖥️ Build Method](#%EF%B8%8F-build-method)를 참고할 것 

## 🔙 Requirements / Solution
- 사용자가 삼쩜삼에 가입해야 합니다.
  - 회원 가입 API 제공  
- 가입한 유저의 정보를 스크랩 하여 환급액이 있는지 조회합니다.
  - 유저의 정보를 스크랩하여 환급액 계산에 필요한 속성을 DB에 저장
- 조회한 금액을 계산한 후 유저에게 실제 환급액을 알려줍니다.
  - 요구사항에 주어진 계산식으로 환급액 계산 후 반환 
- UI를 제외하고 간소화된 REST API만 구현하시면 됩니다.
  - Swagger Link로 REST API 테스트 환경 제공 

## 💎 Main Features
- `Spring Boot` 애플리케이션으로 구성된 HTTP REST API 서버
- `Amazon EC2`와 `Docker`를 사용한 배포
- 다른 사람이 프로젝트 코드를 다운로드하고 실행할 때 H2 Embedded DB를 사용할 수 있으며, **추가적인 설정이나 외부 데이터베이스 연결 없이** 애플리케이션을 실행
  - H2 Embedded DB를 메모리 모드로 사용하고, 애플리케이션을 실행할 때마다 새로운 메모리 데이터베이스(testdb)가 생성되고, 애플리케이션이 종료되면 데이터베이스가 삭제
- 일부러 JWT 토큰의 유효 기간을 1분으로 두어 토큰의 유효기간 만료 여부를 검증하기 편하도록 설정 
- Docker image 빌드 시 **테스트 자동화** 구현
- `GitHub Actions`를 이용한 CI/CD 파이프라인 구성 및 백그라운드 **배포 자동화** 구현
- Docker를 활용한 배포 및 이에 따른 환경 설정 코드 관리 용이
  - **[시스템 아키텍처 설계 개선]** ec2 인스턴스에서 바로 jar 파일 배포 → ec2 내의 docker를 통해 배포
- 서비스 플로우 설명 및 Issue에 문서화한 내용을 기반으로한 피드백을 수용하여 **UX 개선**
- `Nginx`, `Let's Encrypt`를 활용한 `https` 적용

## 📐 Service Architecture
<img width="350" alt="스크린샷 2023-07-04 오후 2 59 06" src="https://github.com/iju1633/3o3-server/assets/43805087/781b36d1-233c-4df8-8764-eee361e5b254" width="200" height="400">

## 🖥️ Build Method
- 해당 [링크](https://cyclic-baboon-a84.notion.site/c907cb289924461b8e8e34b9fa01dc99?pvs=4)는 운영체제별(Mac, Windows) 웹서버를 로컬에서 실행시키는 방법을 설명하고 있습니다.  
or  
- 예시 데이터가 포함된 웹서버를 **배포**해놨으니 [Swagger Link](https://tax-server.kro.kr/swagger-ui/#/)로 구현된 기능을 **프로젝트 빌드 과정 없이** 바로 테스트해보실 수 있습니다.
  - [테스트 방법 가이드](https://cyclic-baboon-a84.notion.site/c72c5c02c725464bb9998d02d9bc00d5?pvs=4)

## 🗄️ ERD
<img width="433" alt="스크린샷 2023-07-13 오후 8 20 18" src="https://github.com/iju1633/3o3-server/assets/43805087/21a54ae1-312f-444d-98d0-97823d795213">

## 📃 API Documentation
[Swagger Link](https://tax-server.kro.kr/swagger-ui/#/)  
<img width="1419" alt="스크린샷 2023-07-13 오후 7 39 16" src="https://github.com/iju1633/3o3-server/assets/43805087/759bd297-261a-4f2b-b4bd-50784ebca30d">  

- [테스트 방법 가이드](https://cyclic-baboon-a84.notion.site/c72c5c02c725464bb9998d02d9bc00d5?pvs=4)
* 이 프로젝트는 클라이언트와의 통신을 위해 swagger Specification 2.0 및 Swagger UI를 활용합니다.  

## 🏛️ Depedency Used
- implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
- implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
- implementation 'org.springframework.boot:spring-boot-starter-security'
- implementation 'org.springframework.boot:spring-boot-starter-validation'
- implementation 'org.springframework.boot:spring-boot-starter-web'
- implementation 'io.springfox:springfox-boot-starter:3.0.0' // swagger
- implementation 'io.jsonwebtoken:jjwt:0.9.1' // jwt
- implementation 'org.apache.httpcomponents:httpclient:4.5.14' // apache httpClient
- compileOnly 'org.projectlombok:lombok'
- runtimeOnly 'com.h2database:h2'
- annotationProcessor 'org.projectlombok:lombok'
- testImplementation 'org.springframework.boot:spring-boot-starter-test'
- testImplementation 'org.springframework.security:spring-security-test'
  
#### Contributor

[Jaeuk Im](https://github.com/iju1633)
|:---:|
|BACKEND|
