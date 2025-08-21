package com.palakendra.palakendra.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("api").pathsToMatch("/api/**").build();
    }
}


