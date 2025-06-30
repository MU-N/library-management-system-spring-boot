package com.nasser.library.util;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle IllegalArgumentException - Bad Request (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle EntityNotFoundException - Not Found (404)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage(),
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle validation errors - Bad Request (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Invalid input parameters",
                "/error"
        );
        errorResponse.put("fieldErrors", fieldErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violations - Bad Request (400)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                ex.getMessage(),
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle missing request parameters - Bad Request (400)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Missing Parameter",
                "Required parameter '" + ex.getParameterName() + "' is missing",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle method argument type mismatch - Bad Request (400)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter Type",
                "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType().getSimpleName(),
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle HTTP message not readable - Bad Request (400)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON",
                "Request body is not readable or malformed",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle unsupported HTTP methods - Method Not Allowed (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle access denied - Forbidden (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You don't have permission to access this resource",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle data integrity violations - Conflict (409)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage());
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Data Integrity Violation",
                "The operation could not be completed due to data constraints",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle RuntimeException - Internal Server Error (500)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred while processing your request",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions - Internal Server Error (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                "/error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to create standardized error response
     */
    private Map<String, Object> createErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }
}