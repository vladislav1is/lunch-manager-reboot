package com.redfox.restaurantvoting.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class NotFoundException extends AppException {
    public NotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg, ErrorAttributeOptions.of(MESSAGE));
    }
}
