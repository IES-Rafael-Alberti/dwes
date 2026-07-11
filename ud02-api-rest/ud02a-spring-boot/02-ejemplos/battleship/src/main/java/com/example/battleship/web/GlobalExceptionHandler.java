package com.example.battleship.web;

import com.example.battleship.domain.exceptions.GameNotFoundException;
import com.example.battleship.dto.ErrorPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ErrorPayload> handleNotFound(GameNotFoundException ex) {
        var payload = new ErrorPayload("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(404).body(payload);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorPayload> handleBadRequest(IllegalArgumentException ex) {
        var payload = new ErrorPayload("BAD_REQUEST", ex.getMessage());
        return ResponseEntity.badRequest().body(payload);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorPayload> handleConflict(IllegalStateException ex) {
        var payload = new ErrorPayload("CONFLICT", ex.getMessage());
        return ResponseEntity.status(409).body(payload);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorPayload> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        var payload = new ErrorPayload("VALIDATION_ERROR", msg);
        return ResponseEntity.badRequest().body(payload);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGeneric(Exception ex) {
        var payload = new ErrorPayload("INTERNAL_ERROR", "Unexpected error");
        return ResponseEntity.status(500).body(payload);
    }
}
