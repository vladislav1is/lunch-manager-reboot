package com.redfox.restaurantvoting.error.vote;

import com.redfox.restaurantvoting.error.DataConflictException;

public class AlreadyVotedException extends DataConflictException {
    public AlreadyVotedException(String msg) {
        super(msg);
    }
}