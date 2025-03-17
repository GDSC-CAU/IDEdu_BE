package com.gdg.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Spring에서 설정 클래스로 사용됨을 명시
public class SwaggerConfig {

    @Bean // Spring 컨텍스트에서 SchrodingerApi 메서드의 반환값을 빈으로 등록
    public OpenAPI SchrodingerApi() {
        // Swagger UI에서 API 문서의 정보를 설정
        Info info = new Info()
                .title("") // API의 제목 설정 (현재 빈 문자열)
                .description("") // API의 설명 설정 (현재 빈 문자열)
                .version("1.0.0"); // API의 버전 설정

        // JWT 인증 스키마의 이름을 정의
        String jwtSchemeName = "JWT TOKEN";

        // Swagger에서 보안을 적용할 때 사용할 SecurityRequirement를 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 보안 스키마를 구성하는 Components를 생성
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName) // 보안 스키마의 이름 설정
                        .type(SecurityScheme.Type.HTTP) // HTTP 인증 방식 사용
                        .scheme("bearer") // Bearer 인증 방식 사용
                        .bearerFormat("JWT")); // Bearer 토큰의 형식이 JWT임을 명시

        // OpenAPI 객체 생성 및 구성
        return new OpenAPI()
                .addServersItem(new Server().url("/")) // 기본 서버 URL 설정 (현재 루트 경로)
                .info(info) // API 정보 추가
                .addSecurityItem(securityRequirement) // 보안 요구 사항 추가
                .components(components); // 보안 스키마를 포함한 컴포넌트 추가
    }
}

