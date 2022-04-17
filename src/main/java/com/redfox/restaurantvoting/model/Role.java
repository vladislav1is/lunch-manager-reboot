package com.redfox.restaurantvoting.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    R_ADMIN; // restaurants admin

    @Override
    public String getAuthority() {
        // https://stackoverflow.com/a/19542316/548473
        return "ROLE_" + name();
    }
}