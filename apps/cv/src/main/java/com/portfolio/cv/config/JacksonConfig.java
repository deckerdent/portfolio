package com.portfolio.cv.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Registers:
     * - {@link JavaTimeModule}: serializes java.time types (OffsetDateTime,
     * LocalDate, …) as ISO strings.
     * - {@link JsonNullableModule}: serializes {@code JsonNullable<T>} as the
     * wrapped value or {@code null}
     * instead of the raw POJO, required by OpenAPI-generated models.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonModulesCustomizer() {
        return builder -> builder.modules(new JavaTimeModule(), new JsonNullableModule());
    }
}
