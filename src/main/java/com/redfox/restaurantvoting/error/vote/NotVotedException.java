package com.redfox.restaurantvoting.error.vote;

import com.redfox.restaurantvoting.error.DataConflictException;

public class NotVotedException extends DataConflictException {
    public NotVotedException(String msg) {
        super(msg);
    }
}