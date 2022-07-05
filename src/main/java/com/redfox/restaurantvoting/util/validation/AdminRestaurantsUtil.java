package com.redfox.restaurantvoting.util.validation;

import com.redfox.restaurantvoting.model.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class AdminRestaurantsUtil {

    private final String adminRestaurants = Pattern.quote("admin/restaurants");
    private final String REGEX = "/(.*?)" + "/(.*?)" + adminRestaurants + "/(\\d+)";

    public static final Pattern MATCHER = Pattern.compile(REGEX);

    public static void checkRequestAccess(User user, String requestURI) {
        Matcher matcher = MATCHER.matcher(requestURI);
        if (matcher.find()) {
            int restaurantId = Integer.parseInt(matcher.group(3));
            log.info("{} request for restaurant {}", user.out(), restaurantId);
            if (!user.getAdminRestaurants().contains(restaurantId)) {
                throw new AccessDeniedException("User '" + user.getEmail() + "' has no rights to restaurant '" + restaurantId);
            }
        }
    }

    public static void addRestaurant(User user, Integer restaurantId) {
        Assert.notNull(restaurantId, "RestaurantId in user.adminRestaurants cannot be null");
        Set<Integer> adminRestaurants = user.getAdminRestaurants();
        if (adminRestaurants == null || adminRestaurants.size() == 0) {
            adminRestaurants = new HashSet<>();
            user.setAdminRestaurants(adminRestaurants);
        }
        adminRestaurants.add(restaurantId);
    }
}
