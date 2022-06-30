package com.redfox.restaurantvoting.error;

public enum ErrorType {
    APP_ERROR("error.appError"),
    DATA_NOT_FOUND("error.dataNotFound"),
    DATA_ERROR("error.dataError"),
    DATA_DISABLED("error.dataDisabled"),
    DEADLINE("error.deadline"),
    NOT_VOTED("error.notVoted"),
    ALREADY_VOTED("error.alreadyVoted"),
    RESTAURANT_CONSTRAINT_VIOLATION("error.restaurantConstraintViolation"),
    DISH_REF_CONSTRAINT_VIOLATION("error.dishRefConstraintViolation"),
    VALIDATION_ERROR("error.validationError"),
    WRONG_REQUEST("error.wrongRequest"),
    FORBIDDEN("error.forbidden");

    private final String errorCode;

    ErrorType(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
