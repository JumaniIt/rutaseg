package com.jumani.rutaseg.handler;

import com.jumani.rutaseg.dto.response.ApiError;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<ApiError> handleResourceNotFoundException(ValidationException exception) {
        final ApiError apiError = new ApiError(exception.getCode(), exception.getMessage());
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ApiError> handleResourceNotFoundException(NotFoundException exception) {
        final ApiError apiError = new ApiError(exception.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

}
