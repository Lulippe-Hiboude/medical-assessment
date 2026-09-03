package com.medical.assessment.riskms.api.controller;


import com.medical.assessment.riskms.api.controller.record.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> ResponseStatusExceptionHandler(final ResponseStatusException e) {
        final ErrorResponse errorResponse = new ErrorResponse(
                e.getStatusCode().value(),
                e.getReason());

        return ResponseEntity.status(e.getStatusCode())
                .body(errorResponse);
    }
}
