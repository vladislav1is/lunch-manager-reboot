package com.redfox.restaurantvoting.error;

public class DishRefConstraintViolationException extends DataConflictException {
    public DishRefConstraintViolationException(String msg) {
        super(msg);
    }
}