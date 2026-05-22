package org.swengineer.global.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(List.of(
                        //new Server().url("나중에 배포한 서버").description("운영 서버"),
                        new Server().url("http://localhost:8080").description("로컬 서버")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("ROUTINER API")
                .description("소프트웨어 공학 프로젝트 ROUTINER API 명세서")
                .version("v1.0.0");
    }
}