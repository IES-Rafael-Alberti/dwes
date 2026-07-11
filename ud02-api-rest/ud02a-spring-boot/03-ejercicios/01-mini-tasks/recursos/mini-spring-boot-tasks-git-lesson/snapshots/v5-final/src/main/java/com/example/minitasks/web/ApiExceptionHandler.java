package com.example.minitasks.web;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
    Map<String,Object> body=new HashMap<>(); body.put("error","Validation failed");
    body.put("fields", ex.getBindingResult().getFieldErrors().stream().map(fe->Map.of("field",fe.getField(),"message",fe.getDefaultMessage())).toList());
    return ResponseEntity.badRequest().body(body);
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,String>> handleIllegalArgument(IllegalArgumentException ex){
    return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
  }
}