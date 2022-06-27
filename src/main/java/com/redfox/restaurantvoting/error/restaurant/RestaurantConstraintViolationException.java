package com.redfox.restaurantvoting.error.restaurant;

import com.redfox.restaurantvoting.error.DataConflictException;

public class RestaurantConstraintViolationException extends DataConflictException {
    public RestaurantConstraintViolationException(String msg) {
        super(msg);
    }
}