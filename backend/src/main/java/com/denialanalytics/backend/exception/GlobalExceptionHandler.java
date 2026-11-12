package com.denialanalytics.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid value for parameter: " + ex.getName(),
                "timestamp", LocalDateTime.now().toString()
            )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBody(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid request body",
                "timestamp", LocalDateTime.now().toString()
            )
        );
    }
}
