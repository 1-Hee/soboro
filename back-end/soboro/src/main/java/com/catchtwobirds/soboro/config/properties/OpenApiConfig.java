package com.catchtwobirds.soboro.config.properties;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        Info info = new Info().title("Soboro API").version("Ver.1.1.0")
                .description("Soboro API문서 입니다.")
//                .termsOfService("http://swagger.io/terms/")\
                .contact(new Contact().name("Soboro").url("http://localhost:8080").email("soboroservice@soboro.com"))
                .license(new License().name("MIT Licence").url("https://opensource.org/licenses/MIT"));

//        // SecuritySecheme명
//        String jwtSchemeName = "jwtAuth";
//        // API 요청헤더에 인증정보 포함
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
//        // SecuritySchemes 등록
//        Components components = new Components()
//                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
//                        .name(jwtSchemeName)
//                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
//                        .scheme("bearer")
//                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

        return new OpenAPI()
//                .addSecurityItem(securityRequirement)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .info(info)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));

    }

}