package com.reactivespring.errorhandler;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.ReviewsClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(value = MoviesInfoClientException.class)
    public ResponseEntity<String> handleMoviesInfoClientException(MoviesInfoClientException ex){
        log.error("Exception caught in MoviesInfoClientException : {}",ex.getMessage() );
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
        log.error("Exception caught in handleRuntimeException : {}",ex.getMessage() );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

}
