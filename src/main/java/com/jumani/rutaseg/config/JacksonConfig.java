package com.jumani.rutaseg.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.autoDetectGettersSetters(true)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .featuresToEnable(
                        DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                        MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS
                );
    }
}
