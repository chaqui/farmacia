package com.inventario.controller;

import java.util.HashMap;
import java.util.Map;

import com.inventario.exception.HttpException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
@ResponseBody
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new java.util.ArrayList<java.util.Map<String, String>>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            var m = new HashMap<String, String>();
            m.put("field", error.getField());
            m.put("message", error.getDefaultMessage());
            errors.add(m);
        });
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        body.put("code", 400);
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        var errors = new java.util.ArrayList<java.util.Map<String, String>>();
        ex.getConstraintViolations().forEach(cv -> {
            var m = new HashMap<String, String>();
            m.put("field", cv.getPropertyPath().toString());
            m.put("message", cv.getMessage());
            errors.add(m);
        });
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        body.put("code", 400);
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<Object> handleHttpException(HttpException ex) {
        int code = ex.getCode() != null ? ex.getCode() : 400;
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("code", code);
        body.put("errors", java.util.Collections.emptyList());
        return new ResponseEntity<>(body, HttpStatus.valueOf(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Internal server error");
        body.put("code", 500);
        body.put("errors", java.util.Collections.emptyList());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
