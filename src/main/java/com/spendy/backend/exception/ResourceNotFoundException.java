package com.spendy.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, String id) {
        super(recurso + " con id " + id + " no fue encontrado");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
