package com.spendy.backend.controller;

import com.spendy.backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> body(String error, String message, HttpStatus status, String path) {
        return Map.of(
                "error", error,
                "message", message,
                "status", status.value(),
                "timestamp", Instant.now().toString(),
                "path", path
        );
    }

    // 404 – recurso no encontrado (excepción personalizada)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body("No encontrado", ex.getMessage(), HttpStatus.NOT_FOUND, req.getRequestURI()));
    }

    // 404 – alternativa por si en algún sitio se lanza NoSuchElementException
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuch(NoSuchElementException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body("No encontrado", ex.getMessage(), HttpStatus.NOT_FOUND, req.getRequestURI()));
    }

    // 400 – argumentos ilegales
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegal(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest()
                .body(body("Solicitud inválida", ex.getMessage(), HttpStatus.BAD_REQUEST, req.getRequestURI()));
    }
}