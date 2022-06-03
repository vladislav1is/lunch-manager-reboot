package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.io.Serial;

@Getter
@ToString(of = "user")
public class AuthUser extends org.springframework.security.core.userdetails.User {
    @Serial
    private static final long serialVersionUID = 1L;

    private User user;

    public AuthUser(@NonNull User user) {
        super(user.getEmail(), user.getPassword(), user.getRoles());
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int id() {
        return user.id();
    }

    public boolean hasRole(Role role) {
        return user.hasRole(role);
    }
}