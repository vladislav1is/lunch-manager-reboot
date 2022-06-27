package com.redfox.restaurantvoting.error.restaurant;

import com.redfox.restaurantvoting.error.DataConflictException;

public class DishRefConstraintViolationException extends DataConflictException {
    public DishRefConstraintViolationException(String msg) {
        super(msg);
    }
}