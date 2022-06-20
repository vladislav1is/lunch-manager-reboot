package com.redfox.restaurantvoting;

public interface HasMenuItemToConstraint extends HasId {
    String getName();
    String getActualDate();

    default String out() {
        return getId() + ":" + getName() + ":" + getActualDate();
    }
}
