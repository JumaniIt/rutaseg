package com.jumani.rutaseg.handler;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<Error> handleValidationException(ValidationException exception) {
        final Error error = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Error> handleResourceNotFoundException(NotFoundException exception) {
        final Error error = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidRequestOriginException.class)
    private ResponseEntity<Error> handleInvalidRequestOriginException(InvalidRequestOriginException exception) {
        final Error error = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    private ResponseEntity<Error> handleUnauthorizedException(UnauthorizedException exception) {
        final Error error = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    private ResponseEntity<Error> handleForbiddenException(ForbiddenException exception) {
        final Error error = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception exception) {
        return this.handleKnownException(exception).orElseGet(() -> {
            final Error error = new Error("internal_error", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        });
    }

    private Optional<ResponseEntity<Error>> handleKnownException(Exception ex) {
        ResponseEntity<Error> response = null;

        if (ex instanceof ValidationException e) response = this.handleValidationException(e);

        if (ex instanceof InvalidRequestOriginException e) response = this.handleInvalidRequestOriginException(e);

        if (ex instanceof UnauthorizedException e) response = this.handleUnauthorizedException(e);

        if (ex instanceof ForbiddenException e) response = this.handleForbiddenException(e);

        return Optional.ofNullable(response);
    }
}
