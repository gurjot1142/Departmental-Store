package com.example.DepartmentalStoreCrud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles NoSuchElementException
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionDetails> handleNoSuchElementException(final NoSuchElementException ex, final WebRequest request) {
        ExceptionDetails error = new ExceptionDetails();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setTrace(getStackTraceAsString(ex));
        error.setPath(request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDetails> handleInvalidArgumentException(final IllegalArgumentException ex,  final WebRequest request) {
        ExceptionDetails error = new ExceptionDetails();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setTrace(getStackTraceAsString(ex));
        error.setPath(request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionDetails> handleOutOfStockException(final IllegalStateException ex, final WebRequest request) {
        ExceptionDetails error = new ExceptionDetails();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.CREATED.value());
        error.setError(HttpStatus.CREATED.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setTrace(getStackTraceAsString(ex));
        error.setPath(request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.CREATED);
    }

    /**
     * Handles IOException
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionDetails> handleIOException(final IOException ex, final WebRequest request) {
        ExceptionDetails error = new ExceptionDetails();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setTrace(getStackTraceAsString(ex));
        error.setPath(request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private String getStackTraceAsString(final Exception ex) {
        StringWriter sw = new StringWriter();
        return sw.toString();
    }
}
