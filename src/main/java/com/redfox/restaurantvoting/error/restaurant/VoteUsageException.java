package com.redfox.restaurantvoting.error.restaurant;

import com.redfox.restaurantvoting.error.DataConflictException;

public class VoteUsageException extends DataConflictException {
    public VoteUsageException(String msg) {
        super(msg);
    }
}