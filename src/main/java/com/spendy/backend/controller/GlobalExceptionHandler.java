package com.spendy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex){
        var fe = ex.getBindingResult().getFieldError();
        String msg = (fe!=null) ? fe.getField()+" "+fe.getDefaultMessage() : "Validation error";
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }
}