package com.jumani.rutaseg.repository.file;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.dto.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class SupabaseFileRepository implements FileRepository {

    private final RestTemplate restTemplate;
    private final String supabaseUrl;
    private final String serviceKey;
    private final String bucket;
    private final long signedUrlTtlSeconds;

    public SupabaseFileRepository(RestTemplate restTemplate,
                                  String supabaseUrl,
                                  String serviceKey,
                                  String bucket,
                                  long signedUrlTtlSeconds) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate");
        this.supabaseUrl = stripTrailingSlash(requireHttps(supabaseUrl));
        this.serviceKey = Objects.requireNonNull(serviceKey, "serviceKey");
        this.bucket = Objects.requireNonNull(bucket, "bucket");
        if (signedUrlTtlSeconds <= 0) {
            throw new IllegalArgumentException("signedUrlTtlSeconds must be > 0");
        }
        this.signedUrlTtlSeconds = signedUrlTtlSeconds;
    }

    @Override
    public Result<Optional<String>> findLinkToFile(String key) {
        final Optional<Error> validation = validateKey(key);
        if (validation.isPresent()) {
            return Result.error(validation.get().code(), validation.get().message());
        }
        try {
            final String url = buildSignUrl(key);
            final HttpHeaders headers = authHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                    Map.of("expiresIn", signedUrlTtlSeconds), headers);

            final ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                final Object signedPath = response.getBody().get("signedURL");
                if (signedPath instanceof String s && !s.isBlank()) {
                    return Result.response(Optional.of(supabaseUrl + s));
                }
            }
            return Result.response(Optional.empty());

        } catch (HttpClientErrorException e) {
            return Result.response(Optional.empty());
        } catch (RestClientException e) {
            final String errorMessage = String.format("could not generate signed URL for key [%s]", key);
            log.error(errorMessage, e);
            return Result.error("file_get_error", errorMessage);
        }
    }

    @Override
    public Optional<Error> save(String key, MultipartFile file) {
        final Optional<Error> validation = validateKey(key);
        if (validation.isPresent()) {
            return validation;
        }
        try {
            final String url = buildObjectUrl(key);
            final HttpHeaders headers = authHeaders();
            final String contentType = file.getContentType() != null
                    ? file.getContentType()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.set("x-upsert", "true");

            final HttpEntity<byte[]> request = new HttpEntity<>(file.getBytes(), headers);
            final ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.empty();
            }
            return Optional.of(new Error("file_save_error", "unexpected response from storage"));

        } catch (IOException | RestClientException e) {
            final String errorMessage = String.format("could not upload file with key [%s]", key);
            log.error(errorMessage, e);
            return Optional.of(new Error("file_save_error", errorMessage));
        }
    }

    @Override
    public Optional<Error> delete(String key) {
        final Optional<Error> validation = validateKey(key);
        if (validation.isPresent()) {
            return validation;
        }
        try {
            final String url = buildObjectUrl(key);
            final HttpHeaders headers = authHeaders();
            final HttpEntity<Void> request = new HttpEntity<>(headers);
            final ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.empty();
            }
            return Optional.of(new Error("file_delete_error", "unexpected response from storage"));

        } catch (HttpClientErrorException e) {
            return Optional.empty();
        } catch (RestClientException e) {
            final String errorMessage = String.format("could not delete file with key [%s]", key);
            log.error(errorMessage, e);
            return Optional.of(new Error("file_delete_error", errorMessage));
        }
    }

    private String buildSignUrl(String key) {
        return appendKeyPath(UriComponentsBuilder.fromHttpUrl(supabaseUrl)
                .pathSegment("storage", "v1", "object", "sign", bucket), key);
    }

    private String buildObjectUrl(String key) {
        return appendKeyPath(UriComponentsBuilder.fromHttpUrl(supabaseUrl)
                .pathSegment("storage", "v1", "object", bucket), key);
    }

    private static String appendKeyPath(UriComponentsBuilder builder, String key) {
        for (String segment : key.split("/")) {
            if (!segment.isEmpty()) {
                builder.pathSegment(segment);
            }
        }
        return builder.build().toUriString();
    }

    private HttpHeaders authHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey);
        return headers;
    }

    private static Optional<Error> validateKey(String key) {
        if (key == null || key.isBlank()) {
            return Optional.of(new Error("file_invalid_key", "key must not be blank"));
        }
        if (key.startsWith("/") || key.contains("..") || key.contains("\\")) {
            return Optional.of(new Error("file_invalid_key", "key contains forbidden characters"));
        }
        return Optional.empty();
    }

    private static String requireHttps(String url) {
        if (url == null || !url.startsWith("https://")) {
            throw new IllegalArgumentException("supabase URL must use https");
        }
        return url;
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
