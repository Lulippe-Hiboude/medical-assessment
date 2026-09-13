package com.medical.assessment.patientms.api.controller;

import com.medical.assessment.patientms.exception.PatientAlreadyExistsException;
import com.medical.assessment.patientms.exception.PatientNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles {@link IllegalArgumentException} exceptions.
     *
     * <p>This exception is mapped to an HTTP {@code 400 BAD REQUEST} response.
     * The exception message is returned as the response body.</p>
     *
     * @param e the exception raised during request processing
     * @return a {@link ResponseEntity} containing the error message with
     *         HTTP status {@code 400 BAD REQUEST}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler(final IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    /**
     * Handles {@link PatientNotFoundException} exceptions.
     *
     * <p>This exception is mapped to an HTTP {@code 404 NOT FOUND} response.
     * The exception message is returned as the response body.</p>
     *
     * @param e the exception raised when a patient cannot be found
     * @return a {@link ResponseEntity} containing the error message with
     *         HTTP status {@code 404 NOT FOUND}
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<String> patientNotFoundExceptionHandler(final PatientNotFoundException e) {
        log.error("Patient not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    /**
     * Handles {@link PatientAlreadyExistsException} exceptions.
     *
     * <p>This exception is mapped to an HTTP {@code 409 CONFLICT} response
     * when a patient already exists.</p>
     *
     * @param e the exception raised when attempting to create an existing patient
     * @return a {@link ResponseEntity} containing the error message with
     *         HTTP status {@code 409 CONFLICT}
     */
    @ExceptionHandler(PatientAlreadyExistsException.class)
    public ResponseEntity<String> patientAlreadyExistsExceptionHandler(final PatientAlreadyExistsException e) {
        log.error("Patient already exists: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    /**
     * Handles {@link ConstraintViolationException} exceptions.
     *
     * <p>This exception is mapped to an HTTP {@code 400 BAD REQUEST} response.
     * The validation error message is returned as the response body.</p>
     *
     * @param ex the exception containing the constraint validation errors
     * @return a {@link ResponseEntity} containing the validation error message
     *         with HTTP status {@code 400 BAD REQUEST}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handle(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles {@link MethodArgumentNotValidException} exceptions raised when
     * request body validation fails.
     *
     * <p>Each validation error is mapped to its corresponding field name and
     * validation message. The resulting map is returned with HTTP status
     * {@code 400 BAD REQUEST}.</p>
     *
     * @param ex the exception containing the validation errors
     * @return a {@link ResponseEntity} containing a map of field names and
     *         validation error messages with HTTP status {@code 400 BAD REQUEST}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles {@link HttpMessageNotReadableException} exceptions raised when
     * the HTTP request body cannot be deserialized.
     *
     * <p>This typically occurs when the request contains malformed JSON or
     * an invalid value that cannot be converted to the expected Java type.</p>
     *
     * @param ex the exception raised during HTTP message deserialization
     * @return a {@link ResponseEntity} containing a generic error message with
     *         HTTP status {@code 400 BAD REQUEST}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException ex) {

        Map<String, String> error = new HashMap<>();

        Throwable cause = ex.getMostSpecificCause();
        log.debug("error : ", cause);
        error.put("message", "Malformed JSON");
        return ResponseEntity.badRequest().body(error);
    }
}
