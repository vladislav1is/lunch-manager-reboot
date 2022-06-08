package com.redfox.restaurantvoting;

public interface HasRestaurantConstraint extends HasId {
    String getName();
    String getAddress();

    default String out() {
        return getId() + ":" + getName() + ":" + getAddress();
    }
}
