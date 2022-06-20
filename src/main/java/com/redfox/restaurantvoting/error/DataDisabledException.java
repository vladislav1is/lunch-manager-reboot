package com.redfox.restaurantvoting.error;

public class DataDisabledException extends DataConflictException {
    public DataDisabledException(String msg) {
        super(msg);
    }
}