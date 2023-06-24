package com.jumani.rutaseg.filter;

import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.InvalidRequestOriginException;
import com.jumani.rutaseg.exception.UnauthorizedException;
import com.jumani.rutaseg.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final List<String> knownOrigins;

    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "access-control-request-headers";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_SUFFIX = "Bearer ";
    public static final String ORIGIN_UUID_HEADER = "origin_uuid";

    // Endpoints que NO necesitan ser autorizados con JWT
    private static final List<String> SKIPPED_ENDPOINTS;
    protected static final List<String> ADMIN_ENDPOINTS;


    static {
        SKIPPED_ENDPOINTS = new ArrayList<>();
    }

    static {
        ADMIN_ENDPOINTS = new ArrayList<>();
    }

    /*
     * Este método se ejecuta en todas las peticiones antes de llegar al controller correspondiente.
     * Cuando se trata de un endpoint que no está en la lista 'skippedEndpoints', lo que hace es extraer el JWT del header y verificar su validez.
     * Si es nulo/vacío/inválido/vencido arroja un error.
     * Si es válido llama al filterChain.doFilter() lo cual significa seguir el curso natural de la petición (ir al controller)
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (this.isPreFlight(request)) return;

        if (!this.isValidRequestOrigin(request)) {
            throw new InvalidRequestOriginException();
        }

        final String invokedEndpoint = request.getRequestURI();
        if (SKIPPED_ENDPOINTS.contains(invokedEndpoint)) return;

        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_SUFFIX)) {
            throw new UnauthorizedException();
        }

        final String token = authorizationHeader.substring(BEARER_SUFFIX.length());
        if (!jwtService.isTokenValid(token)) {
            throw new UnauthorizedException();
        }

        if (ADMIN_ENDPOINTS.stream().anyMatch(invokedEndpoint::startsWith) && !jwtService.isAdminToken(token)) {
            throw new ForbiddenException();
        }

        filterChain.doFilter(request, httpServletResponse);

    }

    private boolean isPreFlight(HttpServletRequest request) {
        return request.getHeader(ACCESS_CONTROL_REQUEST_HEADERS) != null;
    }

    private boolean isValidRequestOrigin(HttpServletRequest request) {
        if (knownOrigins.isEmpty()) {
            return true;
        }

        return Optional.ofNullable(request.getHeader(ORIGIN_UUID_HEADER))
                .map(knownOrigins::contains)
                .orElse(false);
    }
}