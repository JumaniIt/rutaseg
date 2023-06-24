package com.jumani.rutaseg.config;

import com.jumani.rutaseg.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class JwtConfig {
    @Bean
    @Profile("!local & !integration_test")
    public JwtService jwtService(@Value("${jwt.secretKey}") String secretKey) {
        return new JwtService(secretKey);
    }

    @Bean
    @Profile("local | integration_test")
    public JwtService jwtServiceDev() {
        return new JwtService("secret-key-dev");
    }
}
