package com.jumani.rutaseg.config;

import com.jumani.rutaseg.repository.file.SupabaseFileRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@Profile("!local & !integration_test")
@ConditionalOnProperty(name = "files.provider", havingValue = "supabase")
public class SupabaseConfig {

    @Value("${files.supabase.url}")
    private String supabaseUrl;

    @Value("${files.supabase.service-key}")
    private String serviceKey;

    @Value("${files.supabase.bucket}")
    private String bucket;

    @Value("${files.supabase.signed-url-ttl-seconds:259200}")
    private long signedUrlTtlSeconds;

    @Bean("supabaseRestTemplate")
    public RestTemplate supabaseRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    public SupabaseFileRepository supabaseFileRepository(
            @Qualifier("supabaseRestTemplate") RestTemplate restTemplate) {
        return new SupabaseFileRepository(
                restTemplate, supabaseUrl, serviceKey, bucket, signedUrlTtlSeconds);
    }
}
