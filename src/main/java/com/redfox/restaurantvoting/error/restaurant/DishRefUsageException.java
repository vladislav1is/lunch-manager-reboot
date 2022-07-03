package com.redfox.restaurantvoting.error.restaurant;

import com.redfox.restaurantvoting.error.DataConflictException;

public class DishRefUsageException extends DataConflictException {
    public DishRefUsageException(String msg) {
        super(msg);
    }
}