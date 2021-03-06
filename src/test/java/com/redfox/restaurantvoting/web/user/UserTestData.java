package com.redfox.restaurantvoting.web.user;

import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.util.JsonUtil;
import com.redfox.restaurantvoting.web.MatcherFactory;
import com.redfox.restaurantvoting.web.MatcherFactory.Matcher;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.*;
import static java.time.LocalDateTime.now;

public class UserTestData {
    public static final Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "password", "adminRestaurants");

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;
    public static final int R_ADMIN_ID = 3;
    public static final int NOT_FOUND = 100;

    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";
    public static final String R_ADMIN_MAIL = "r_admin@gmail.com";
    public static final String MAIL_NOT_FOUND = "notfound@gmail.com";

    public static final User user = new User(USER_ID, "User", USER_MAIL, "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.USER, Role.ADMIN);
    public static final User r_admin = new User(R_ADMIN_ID, "Restaurants Admin", R_ADMIN_MAIL, "radmin", Role.USER, Role.R_ADMIN);

    static {
        r_admin.setAdminRestaurants(Set.of(DODO_ID, TEREMOK_ID));
    }

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", false, now().truncatedTo(ChronoUnit.MINUTES), Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "Updated", USER_MAIL, "newPass", false, now().truncatedTo(ChronoUnit.MINUTES), EnumSet.of(Role.USER, Role.ADMIN));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}
