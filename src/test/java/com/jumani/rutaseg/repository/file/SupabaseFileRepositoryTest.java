package com.jumani.rutaseg.repository.file;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.dto.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SupabaseFileRepositoryTest {

    private static final String SUPABASE_URL = "https://test.supabase.co";
    private static final String SERVICE_KEY = "service-role-key-xxx";
    private static final String BUCKET = "files";
    private static final long TTL = 3600L;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private SupabaseFileRepository repository;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        repository = new SupabaseFileRepository(restTemplate, SUPABASE_URL, SERVICE_KEY, BUCKET, TTL);
    }

    @Test
    void findLinkToFile_returnsFullSignedUrl_onSuccess() {
        final String signedPath = "/storage/v1/object/sign/files/cabina-1/pago.pdf?token=abc";
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/sign/" + BUCKET + "/cabina-1/pago.pdf"))
                .andExpect(method(POST))
                .andExpect(header("Authorization", "Bearer " + SERVICE_KEY))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.expiresIn").value((int) TTL))
                .andRespond(withSuccess(
                        "{\"signedURL\":\"" + signedPath + "\"}",
                        MediaType.APPLICATION_JSON));

        final Result<Optional<String>> result = repository.findLinkToFile("cabina-1/pago.pdf");

        assertTrue(result.isSuccessful());
        assertTrue(result.getResponse().isPresent());
        assertEquals(SUPABASE_URL + signedPath, result.getResponse().get());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_returnsEmpty_on4xx() {
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/sign/" + BUCKET + "/missing.pdf"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final Result<Optional<String>> result = repository.findLinkToFile("missing.pdf");

        assertTrue(result.isSuccessful());
        assertTrue(result.getResponse().isEmpty());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_returnsError_on5xx() {
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/sign/" + BUCKET + "/file.pdf"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final Result<Optional<String>> result = repository.findLinkToFile("file.pdf");

        assertFalse(result.isSuccessful());
        assertEquals("file_get_error", result.getError().code());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_returnsEmpty_whenSignedUrlMissingFromBody() {
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/sign/" + BUCKET + "/file.pdf"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        final Result<Optional<String>> result = repository.findLinkToFile("file.pdf");

        assertTrue(result.isSuccessful());
        assertTrue(result.getResponse().isEmpty());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_rejectsKeyWithDotDot_withoutNetworkCall() {
        final Result<Optional<String>> result = repository.findLinkToFile("../etc/passwd");

        assertFalse(result.isSuccessful());
        assertEquals("file_invalid_key", result.getError().code());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_rejectsKeyWithLeadingSlash() {
        final Result<Optional<String>> result = repository.findLinkToFile("/leading.pdf");

        assertFalse(result.isSuccessful());
        assertEquals("file_invalid_key", result.getError().code());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_rejectsBlankKey() {
        final Result<Optional<String>> result = repository.findLinkToFile("  ");

        assertFalse(result.isSuccessful());
        assertEquals("file_invalid_key", result.getError().code());
        mockServer.verify();
    }

    @Test
    void findLinkToFile_rejectsNullKey() {
        final Result<Optional<String>> result = repository.findLinkToFile(null);

        assertFalse(result.isSuccessful());
        assertEquals("file_invalid_key", result.getError().code());
        mockServer.verify();
    }

    @Test
    void save_uploadsFileWithUpsertHeader_returningEmpty() {
        final byte[] content = "pdf-bytes".getBytes();
        final MultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", content);

        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/cabina-1/test.pdf"))
                .andExpect(method(POST))
                .andExpect(header("Authorization", "Bearer " + SERVICE_KEY))
                .andExpect(header("Content-Type", "application/pdf"))
                .andExpect(header("x-upsert", "true"))
                .andExpect(content().bytes(content))
                .andRespond(withStatus(HttpStatus.OK).body("{\"Key\":\"files/cabina-1/test.pdf\"}"));

        final Optional<Error> error = repository.save("cabina-1/test.pdf", file);

        assertTrue(error.isEmpty());
        mockServer.verify();
    }

    @Test
    void save_usesOctetStream_whenContentTypeMissing() {
        final MultipartFile file = new MockMultipartFile(
                "file", "blob", null, new byte[]{1, 2, 3});

        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/blob"))
                .andExpect(header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andRespond(withStatus(HttpStatus.OK));

        final Optional<Error> error = repository.save("blob", file);

        assertTrue(error.isEmpty());
        mockServer.verify();
    }

    @Test
    void save_returnsError_on5xx() {
        final MultipartFile file = new MockMultipartFile(
                "file", "x.pdf", "application/pdf", new byte[]{1, 2, 3});

        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/x.pdf"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final Optional<Error> error = repository.save("x.pdf", file);

        assertTrue(error.isPresent());
        assertEquals("file_save_error", error.get().code());
        mockServer.verify();
    }

    @Test
    void save_rejectsInvalidKey_withoutNetworkCall() {
        final MultipartFile file = new MockMultipartFile(
                "file", "x.pdf", "application/pdf", new byte[0]);

        final Optional<Error> error = repository.save("../escape", file);

        assertTrue(error.isPresent());
        assertEquals("file_invalid_key", error.get().code());
        mockServer.verify();
    }

    @Test
    void delete_returnsEmpty_onSuccess() {
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/test.pdf"))
                .andExpect(method(DELETE))
                .andExpect(header("Authorization", "Bearer " + SERVICE_KEY))
                .andRespond(withStatus(HttpStatus.OK));

        final Optional<Error> error = repository.delete("test.pdf");

        assertTrue(error.isEmpty());
        mockServer.verify();
    }

    @Test
    void delete_idempotent_on4xx() {
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/missing.pdf"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final Optional<Error> error = repository.delete("missing.pdf");

        assertTrue(error.isEmpty());
        mockServer.verify();
    }

    @Test
    void delete_returnsError_on5xx() {
        mockServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/file.pdf"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        final Optional<Error> error = repository.delete("file.pdf");

        assertTrue(error.isPresent());
        assertEquals("file_delete_error", error.get().code());
        mockServer.verify();
    }

    @Test
    void delete_rejectsInvalidKey_withoutNetworkCall() {
        final Optional<Error> error = repository.delete("..");

        assertTrue(error.isPresent());
        assertEquals("file_invalid_key", error.get().code());
        mockServer.verify();
    }

    @Test
    void constructor_rejectsHttpUrl() {
        assertThrows(IllegalArgumentException.class, () ->
                new SupabaseFileRepository(restTemplate, "http://insecure.example.com", SERVICE_KEY, BUCKET, TTL));
    }

    @Test
    void constructor_rejectsNullSupabaseUrl() {
        assertThrows(IllegalArgumentException.class, () ->
                new SupabaseFileRepository(restTemplate, null, SERVICE_KEY, BUCKET, TTL));
    }

    @Test
    void constructor_rejectsZeroTtl() {
        assertThrows(IllegalArgumentException.class, () ->
                new SupabaseFileRepository(restTemplate, SUPABASE_URL, SERVICE_KEY, BUCKET, 0));
    }

    @Test
    void constructor_rejectsNegativeTtl() {
        assertThrows(IllegalArgumentException.class, () ->
                new SupabaseFileRepository(restTemplate, SUPABASE_URL, SERVICE_KEY, BUCKET, -1));
    }

    @Test
    void constructor_rejectsNullServiceKey() {
        assertThrows(NullPointerException.class, () ->
                new SupabaseFileRepository(restTemplate, SUPABASE_URL, null, BUCKET, TTL));
    }

    @Test
    void constructor_rejectsNullBucket() {
        assertThrows(NullPointerException.class, () ->
                new SupabaseFileRepository(restTemplate, SUPABASE_URL, SERVICE_KEY, null, TTL));
    }

    @Test
    void supabaseUrlTrailingSlash_isNormalized() {
        final RestTemplate localRt = new RestTemplate();
        final MockRestServiceServer localServer = MockRestServiceServer.createServer(localRt);
        final SupabaseFileRepository repo = new SupabaseFileRepository(
                localRt, SUPABASE_URL + "/", SERVICE_KEY, BUCKET, TTL);

        localServer.expect(requestTo(SUPABASE_URL + "/storage/v1/object/sign/" + BUCKET + "/file.pdf"))
                .andRespond(withSuccess(
                        "{\"signedURL\":\"/storage/v1/object/sign/files/file.pdf?token=t\"}",
                        MediaType.APPLICATION_JSON));

        final Result<Optional<String>> result = repo.findLinkToFile("file.pdf");

        assertTrue(result.isSuccessful());
        assertEquals(SUPABASE_URL + "/storage/v1/object/sign/files/file.pdf?token=t",
                result.getResponse().get());
        localServer.verify();
    }
}
