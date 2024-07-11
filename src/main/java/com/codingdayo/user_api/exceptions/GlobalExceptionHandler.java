package com.codingdayo.user_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<Map<String, String>>> handleInvalidArgument(MethodArgumentNotValidException exception) {
        List<Map<String, String>> errors = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("field", error.getField());
            errorDetails.put("message", error.getDefaultMessage());
            errors.add(errorDetails);
        });

        return Map.of("errors", errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity <Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    //@ExceptionHandler(BadCredentialsException.class)
    //public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException exception) {
    //    Map<String, String> errorResponse = new HashMap<>();
    //    errorResponse.put("error", exception.getMessage());
    //
    //    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    //}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "Bad request");
        errorResponse.put("message", "Authentication failed");
        errorResponse.put("statusCode", HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

}
