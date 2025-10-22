package com.spendy.backend.controller;

import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonPatchException.class)
    public ResponseEntity<?> handleJsonPatch(JsonPatchException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON-Patch: " + ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Bad request body: " + ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMedia(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Map.of("error", "Unsupported Content-Type. Use application/json-patch+json"));
    }
}