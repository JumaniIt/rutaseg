package com.jumani.rutaseg.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumani.rutaseg.RutasegApplication;
import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static com.jumani.rutaseg.TestDataGen.randomShortString;
import static org.apache.commons.lang3.reflect.FieldUtils.readStaticField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RutasegApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionFilterFunctionalTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String KNOWN_ORIGIN = UUID.randomUUID().toString();

    private static final String SKIPPED_ENDPOINT = "/skipped_endpoint";
    private static final String ADMIN_ENDPOINT = "/admin_endpoint";

    private SessionFilter sessionFilter;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void before() throws IllegalAccessException {
        this.sessionFilter = new SessionFilter(jwtService, List.of(KNOWN_ORIGIN), false);
        final List<String> skippedEndpoints = (List<String>) readStaticField(SessionFilter.class, "SKIPPED_ENDPOINTS", true);
        skippedEndpoints.add(SKIPPED_ENDPOINT);

        final List<String> adminEndpoints = (List<String>) readStaticField(SessionFilter.class, "ADMIN_ENDPOINTS", true);
        adminEndpoints.add(ADMIN_ENDPOINT);

        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new ExceptionFilter())
                .addFilter(sessionFilter, "/*")
                .build();
    }

    @Test
    void doFilterInternal_PreFlightRequest_Ok() throws Exception {
        final MvcResult mvcResult = mvc.perform(get("/simple-get")
                        .header(SessionFilter.ACCESS_CONTROL_REQUEST_HEADERS, "some-value"))
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
    }

    @Test
    void doFilterInternal_InvalidOrigin_BadRequest() throws Exception {
        final MvcResult mvcResult = mvc.perform(get("/simple-get"))
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertEquals("invalid_request_origin", this.parseError(response).code()),
                () -> assertEquals("request origin is invalid", this.parseError(response).message())
        );
    }

    @Test
    void doFilterInternal_SkippedEndpoint_Ok() throws Exception {
        mvc.perform(get(SKIPPED_ENDPOINT)
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
    }

    @Test
    void doFilterInternal_WithoutAuthorizationHeader_Unauthorized() throws Exception {
        final MvcResult mvcResult = mvc.perform(get("/simple-get")
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN))
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
                () -> assertEquals("unauthorized", this.parseError(response).code()),
                () -> assertEquals("invalid or missing session credentials", this.parseError(response).message())
        );
    }

    @Test
    void doFilterInternal_InvalidAuthorizationHeader_Unauthorized() throws Exception {
        final MvcResult mvcResult = mvc.perform(get("/simple-get")
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN)
                        .header(SessionFilter.AUTHORIZATION_HEADER, "invalid-auth-header"))
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
                () -> assertEquals("unauthorized", this.parseError(response).code()),
                () -> assertEquals("invalid or missing session credentials", this.parseError(response).message())
        );
    }

    @Test
    void doFilterInternal_InvalidToken_Unauthorized() throws Exception {
        final String token = randomShortString();

        when(jwtService.isTokenValid(token)).thenReturn(false);

        final MvcResult mvcResult = mvc.perform(get("/simple-get")
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN)
                        .header(SessionFilter.AUTHORIZATION_HEADER, SessionFilter.BEARER_SUFFIX + token))
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
                () -> assertEquals("unauthorized", this.parseError(response).code()),
                () -> assertEquals("invalid or missing session credentials", this.parseError(response).message())
        );
    }

    @Test
    void doFilterInternal_AdminEndpoint_TokenNotAdmin_Forbidden() throws Exception {
        final String token = randomShortString();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.isAdminToken(token)).thenReturn(false);

        final MvcResult mvcResult = mvc.perform(get(ADMIN_ENDPOINT)
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN)
                        .header(SessionFilter.AUTHORIZATION_HEADER, SessionFilter.BEARER_SUFFIX + token))
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus()),
                () -> assertEquals("forbidden", this.parseError(response).code()),
                () -> assertEquals("insufficient privileges to access this resource", this.parseError(response).message())
        );
    }

    @Test
    void doFilterInternal_Ok() throws Exception {
        final String token = randomShortString();

        when(jwtService.isTokenValid(token)).thenReturn(true);

        mvc.perform(get("/simple-get")
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN)
                        .header(SessionFilter.AUTHORIZATION_HEADER, SessionFilter.BEARER_SUFFIX + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
    }

    @Test
    void doFilterInternal_AdminEndpoint_Ok() throws Exception {
        final String token = randomShortString();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.isAdminToken(token)).thenReturn(true);

        mvc.perform(get(ADMIN_ENDPOINT)
                        .header(SessionFilter.ORIGIN_HEADER, KNOWN_ORIGIN)
                        .header(SessionFilter.AUTHORIZATION_HEADER, SessionFilter.BEARER_SUFFIX + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
    }

    @Test
    void doFilterInternal_AllowAllOrigins_Ok() throws Exception {
        writeField(sessionFilter, "allowAllOrigins", true, true);

        final String token = randomShortString();

        when(jwtService.isTokenValid(token)).thenReturn(true);

        mvc.perform(get("/simple-get")
                        .header(SessionFilter.AUTHORIZATION_HEADER, SessionFilter.BEARER_SUFFIX + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
    }

    private Error parseError(MockHttpServletResponse response) {
        try {
            final String responseStr = response.getContentAsString();
            return objectMapper.readValue(responseStr, Error.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
