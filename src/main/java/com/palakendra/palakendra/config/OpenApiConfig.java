package com.palakendra.palakendra.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean GroupedOpenApi publicApi() {

        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .packagesToScan(
                        "com.palakendra.palakendra.web",        // put your controller packages here
                        "com.palakendra.palakendra.controller"  // (add/remove as appropriate)
                )
                .build();
    }
}


