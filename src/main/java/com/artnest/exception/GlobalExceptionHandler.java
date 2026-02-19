package com.artnest.exception;

import com.artnest.dto.ApiError;
import com.artnest.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse("RESOURCE_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponse("CONFLICT", ex.getMessage(), null));
    }

    @ExceptionHandler({AuthenticationFailureException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthentication(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse("INVALID_CREDENTIALS", ex.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse(
                        "ACCESS_DENIED",
                        "You do not have permission to access this resource",
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse("VALIDATION_FAILED", "Validation failed", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableMessage() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse("MALFORMED_REQUEST", "Request body is missing or malformed", null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponse(
                        "DUPLICATE_RESOURCE",
                        "Email or mobile number already exists",
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(
            Exception ex, HttpServletRequest request) throws Exception {

        String path = request.getRequestURI();
        if (path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger")) {
            throw ex;
        }

        String message = String.format(
                "Unexpected server error while processing %s %s",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("INTERNAL_SERVER_ERROR", message, null));
    }


    private ApiResponse<Object> errorResponse(String code, String message, Map<String, ?> details) {
        return new ApiResponse<>(false, message, new ApiError(code, message, details));
    }
}
