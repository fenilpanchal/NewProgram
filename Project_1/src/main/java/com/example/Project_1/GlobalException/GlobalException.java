package com.example.Project_1.GlobalException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> runTimeException(RuntimeException re){
        Map<String, Object> runTime = new HashMap<>();
        runTime.put("Timestamp",LocalDateTime.now());
        runTime.put("Status",HttpStatus.NOT_FOUND.value());
        runTime.put("Error","404 Not Found");
        runTime.put("Message",re.getMessage());
        return new ResponseEntity<>(runTime,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> usernameNotFoundException(UsernameNotFoundException unfe) {
        Map<String, Object> notFound = new HashMap<>();
        notFound.put("Timestamp", LocalDateTime.now());
        notFound.put("Status", HttpStatus.NOT_FOUND.value());
        notFound.put("Error", "User Not Found");
        notFound.put("Message", unfe.getMessage());
        return new ResponseEntity<>(notFound, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,Object>> badCredentials (BadCredentialsException bx){
        Map<String,Object> object = new HashMap<>();
        object.put("Timestamp", LocalDateTime.now());
        object.put("Status",HttpStatus.UNAUTHORIZED.value());
        object.put("Error","Unauthorized");
        object.put("Message", "Invalid username or password.");
        return new ResponseEntity<>(object, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> exception = new HashMap<>();
        exception.put("Timestamp", LocalDateTime.now());
        exception.put("Status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        exception.put("Error", "Internal Server Error");
        exception.put("Message", ex.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
