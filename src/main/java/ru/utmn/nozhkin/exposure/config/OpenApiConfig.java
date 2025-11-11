package ru.utmn.nozhkin.exposure.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI exposureApi() {
        return new OpenAPI().info(new Info()
                .title("Exposure API")
                .version("v1")
                .description("CRUD по данным из exposure.csv, для курса \"Бэкенд разработка: разработка серверной части программного приложения на Java\n\""));
    }
}
