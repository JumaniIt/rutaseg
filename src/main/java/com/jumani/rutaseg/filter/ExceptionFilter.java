package com.jumani.rutaseg.filter;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.handler.ControllerExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class ExceptionFilter extends OncePerRequestFilter {
    private final ControllerExceptionHandler exceptionHandler = new ControllerExceptionHandler();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            final ResponseEntity<Error> error = exceptionHandler.handleException(e);

            response.setStatus(error.getStatusCode().value());
            response.getWriter().write(Optional.ofNullable(error.getBody()).map(Error::serialize).orElse(""));
            response.getWriter().flush();
        }
    }
}
