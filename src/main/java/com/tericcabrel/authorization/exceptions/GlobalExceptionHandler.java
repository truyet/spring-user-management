package com.tericcabrel.authorization.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.tericcabrel.authorization.models.common.ApiResponse;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ApiResponse response = new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<?> passwordNotMatchException(PasswordNotMatchException ex, WebRequest request) {
        ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> fileNotFoundException(FileNotFoundException ex, WebRequest request) {
        ApiResponse response = new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<?> fileStorageException(FileStorageException ex, WebRequest request) {
        ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        HashMap<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(cv -> {
            String[] strings = cv.getPropertyPath().toString().split("\\.");

            errors.put(strings[strings.length - 1], cv.getMessage());
        });

        HashMap<String, HashMap<String,String>> result = new HashMap<>();
        result.put("errors", errors);

        ApiResponse response = new ApiResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), result);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        HashMap<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();

            if (errors.containsKey(field)) {
                List<String> strings = errors.get(field);
                strings.add(fieldError.getDefaultMessage());

                errors.put(field, strings);
            } else {
                List<String> strings = new ArrayList<>();
                strings.add(fieldError.getDefaultMessage());

                errors.put(field, strings);
            }
        });

        HashMap<String, HashMap<String, List<String>>> result = new HashMap<>();
        result.put("errors", errors);

        ApiResponse response = new ApiResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), result);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        ex.printStackTrace();

        ApiResponse response = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}