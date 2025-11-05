package com.spendy.backend.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestControllerAdvice
public class ValidationExceptionHandler {

    /* ---------- Método auxiliar para cuerpo uniforme ---------- */
    private Map<String, Object> body(String error, Object errors, HttpStatus status) {
        return Map.of(
                "error", error,
                "errors", errors,
                "status", status.value(),
                "timestamp", Instant.now().toString()
        );
    }

    /* ---------- 1) Validación de DTOs con @Valid ---------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarValidacionDeDTO(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "campo", fe.getField(),
                        "mensaje", Optional.ofNullable(fe.getDefaultMessage()).orElse("Valor no válido")
                ))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(body("Error de validación", errores, HttpStatus.BAD_REQUEST));
    }

    /* ---------- 2) Validación de parámetros (query/path) con @Validated ---------- */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> manejarRestricciones(ConstraintViolationException ex) {
        List<Map<String, String>> errores = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of(
                        "parametro", v.getPropertyPath().toString(),
                        "mensaje", v.getMessage()
                ))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(body("Violación de restricción", errores, HttpStatus.BAD_REQUEST));
    }

    /* ---------- 3) JSON mal formado o valores imposibles de deserializar ---------- */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> manejarJSONNoLegible(HttpMessageNotReadableException ex) {
        String mensaje = "JSON mal formado o valor no válido";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String tipo = (ife.getTargetType() != null)
                    ? ife.getTargetType().getSimpleName()
                    : "tipo desconocido";
            mensaje = "Valor no válido para el tipo " + tipo;
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body("Cuerpo de solicitud incorrecto", List.of(Map.of("mensaje", mensaje)), HttpStatus.BAD_REQUEST));
    }

    /* ---------- 4) Tipos incorrectos en query/path (por ejemplo, amount=abc) ---------- */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        Map<String, String> err = Map.of(
                "parametro", ex.getName(),
                "esperado", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido",
                "valor", Objects.toString(ex.getValue(), "null")
        );

        return ResponseEntity
                .badRequest()
                .body(body("Tipo de parámetro no válido", List.of(err), HttpStatus.BAD_REQUEST));
    }

    /* ---------- 5) Errores de binding fuera de @RequestBody ---------- */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> manejarErrorDeEnlace(BindException ex) {
        List<Map<String, String>> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "campo", fe.getField(),
                        "mensaje", Optional.ofNullable(fe.getDefaultMessage()).orElse("Valor no válido")
                ))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(body("Error al enlazar datos", errores, HttpStatus.BAD_REQUEST));
    }

    /* ---------- 6) Captura general de errores no controlados ---------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body("Error inesperado", List.of(Map.of("mensaje", ex.getMessage())), HttpStatus.BAD_REQUEST));
    }
}