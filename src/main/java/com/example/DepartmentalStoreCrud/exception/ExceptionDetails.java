package com.example.DepartmentalStoreCrud.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionDetails {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String trace;
    private String path;
}
