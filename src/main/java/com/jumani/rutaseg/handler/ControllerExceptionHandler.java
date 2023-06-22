package com.jumani.rutaseg.handler;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<Error> handleResourceNotFoundException(ValidationException exception) {
        final Error apiError = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Error> handleResourceNotFoundException(NotFoundException exception) {
        final Error apiError = new Error(exception.getCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

}
