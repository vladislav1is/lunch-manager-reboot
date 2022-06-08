package com.redfox.restaurantvoting.error;

public class RestaurantConstraintViolationException extends DataConflictException {
    public RestaurantConstraintViolationException(String msg) {
        super(msg);
    }
}