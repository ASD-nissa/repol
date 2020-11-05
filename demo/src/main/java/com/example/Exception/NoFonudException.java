package com.example.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFonudException extends RuntimeException {
    public NoFonudException(String message) {
        super(message);
    }
}
