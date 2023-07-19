package com.tax.o3server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig { // Swagger 설정 관리

    @Bean
    public OpenAPI customOpenAPI() { // API 정보와 보안 스키마 정보를 추가
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components()
                .addSecuritySchemes("bearerAuth", createAPIKeyScheme()))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private SecurityScheme createAPIKeyScheme() { // Bearer 토큰을 사용하는 API 키 스키마를 생성. JWT 형식의 토큰을 사용하며, HTTP 사용
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    private Info apiInfo() { // API 문서의 제목과 설명을 설정
        return new Info()
                .title("세금 환급 서비스")
                .description("로그인 성공 시 토큰을 생성되어 Authorize라 쓰인 초록색 버튼에 token 값을 입력해 인증을 진행합니다.\n" +
                        "token의 지속시간은 1분입니다."
                );
    }

    @Bean
    public Docket swaggerAPI() { // API 정보, 패키지 스캔 범위, 경로 설정
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.swaggerInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tax.o3server.controller")) // request handler selector가 스캔할 패키지 선택
                .paths(PathSelectors.any()) // 모든 url에 대해 명세서 작성
                .build()
                .useDefaultResponseMessages(true)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()));
    }

    private ApiInfo swaggerInfo() { // 스웨거 문서의 제목, 설명, 버전 설정
        return new ApiInfoBuilder()
                .title("tax-server API Documentation")
                .description("SpringBoot로 개발한 웹 프로젝트입니다.")
                .version("1.0")
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() { // bearerAuth로 정의된 보안 스키마를 사용하고, 권한 범위는 "global"로 설정
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("bearerAuth", authorizationScopes));
    }

    private ApiKey apiKey() { // bearerAuth로 정의된 키를 사용하며, "Authorization" 헤더에 키 값을 포함
        return new ApiKey("bearerAuth", "Authorization", "header");
    }
}
