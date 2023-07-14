# 👕 2023, 삼쩜삼 JAVA 백엔드 엔지니어 채용 Assignment

 유저의 환급액을 계산해 주는 서비스입니다.

## 💪 Skill Stack
![Java](https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=JAVA&logoColor=white)
![Spring-Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white)
![H2](https://img.shields.io/badge/H2-4479A1.svg?&style=for-the-badge&logo=H2&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-66E851?style=for-the-badge&logo=Swagger&logoColor=white)

## 🛠️ Tool
![Git](https://img.shields.io/badge/Git-F05032.svg?&style=for-the-badge&logo=Git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-000000.svg?&style=for-the-badge&logo=Github&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/ItelliJ%20IDEA-4A93D7.svg?&style=for-the-badge&logo=intellij-idea&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000.svg?&style=for-the-badge&logo=Notion&logoColor=white)

## 📖 Explanation
- 회원 가입 API 제공
- 로그인 API 제공
- 회원 정보 반환 API 제공
- 유저의 정보를 스크랩
- 유저의 스크랩 정보를 바탕으로 유저의 결정세액과 퇴직연금세액공제금액을 계산

## 🔙 Requirements / Solution
- 사용자가 삼쩜삼에 가입해야 합니다.
  - 회원 가입 API 제공  
- 가입한 유저의 정보를 스크랩 하여 환급액이 있는지 조회합니다.
  - 유저의 정보를 스크랩하여 환급액 계산에 필요한 속성을 DB에 저장
- 조회한 금액을 계산한 후 유저에게 실제 환급액을 알려줍니다.
  - 주어진 계산식으로 환급액 계산 후 반환 
- UI를 제외하고 간소화된 REST API만 구현하시면 됩니다.
  - Swagger Link로 REST API 테스트 환경 제공 

## 💎 Main Features
- `Spring Boot` 애플리케이션으로 구성된 HTTP REST API 서버
- 다른 사람이 프로젝트 코드를 다운로드하고 실행할 때 H2 Embedded DB를 사용할 수 있으며, **추가적인 설정이나 외부 데이터베이스 연결 없이** 애플리케이션을 실행
  - H2 Embedded DB를 메모리 모드로 사용하고, 애플리케이션을 실행할 때마다 새로운 메모리 데이터베이스(testdb)가 생성되고, 애플리케이션이 종료되면 데이터베이스가 삭제
- 서비스 플로우 설명 및 Issue에 문서화한 내용을 기반으로한 피드백을 수용하여 **UX 개선**

## 📐 Service Architecture

## 🖥️ Build Method
 
## 📝 Documentation (#Issue)
- [프로젝트 구현 조건](https://github.com/iju1633/3o3-server/issues/3)
- [브랜치 & 코드 & PR 관리 전략](https://github.com/iju1633/3o3-server/issues/1)
- [에러 핸들링](https://github.com/iju1633/3o3-server/issues/2)

## 🗄️ ERD
<img width="433" alt="스크린샷 2023-07-13 오후 8 20 18" src="https://github.com/iju1633/3o3-server/assets/43805087/21a54ae1-312f-444d-98d0-97823d795213">

## 📃 API Documentation
<img width="1415" alt="스크린샷 2023-07-13 오후 8 00 16" src="https://github.com/iju1633/3o3-server/assets/43805087/8db352e3-d71f-4d41-aae9-04bf3ad3398a">
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
