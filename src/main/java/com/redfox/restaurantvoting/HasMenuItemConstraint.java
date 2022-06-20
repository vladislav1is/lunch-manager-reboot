package com.redfox.restaurantvoting;

import java.time.LocalDate;

public interface HasMenuItemConstraint extends HasId {
    Integer getDishRefId();
    LocalDate getActualDate();

    default String out() {
        return getId() + ":" + getDishRefId() + ":" + getActualDate();
    }
}
