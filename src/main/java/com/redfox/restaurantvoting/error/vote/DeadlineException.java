package com.redfox.restaurantvoting.error.vote;

import com.redfox.restaurantvoting.error.DataConflictException;

public class DeadlineException extends DataConflictException {
    public DeadlineException(String msg) {
        super(msg);
    }
}