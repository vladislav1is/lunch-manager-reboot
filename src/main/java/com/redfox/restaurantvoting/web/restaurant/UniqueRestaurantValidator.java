package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.HasRestaurantConstraint;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

import static com.redfox.restaurantvoting.web.GlobalExceptionHandler.EXCEPTION_DUPLICATE_NAME_AND_ADDRESS;

@Component
@AllArgsConstructor
public class UniqueRestaurantValidator implements org.springframework.validation.Validator {

    private final RestaurantRepository repository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return HasRestaurantConstraint.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        HasRestaurantConstraint restaurant = ((HasRestaurantConstraint) target);

        if (StringUtils.hasText(restaurant.getName())) {
            repository.findByNameAndAddress(restaurant.getName().strip(), restaurant.getAddress().strip())
                    .ifPresent(dbRestaurant -> {
                        if (request.getMethod().equals("PUT") || request.getMethod().equals("POST")) {  // UPDATE
                            int dbId = dbRestaurant.id();

                            // it is ok, if update ourself
                            if (restaurant.getId() != null && dbId == restaurant.id()) return;

                            // Workaround for update with restaurant.id=null in request body
                            // ValidationUtil.assureIdConsistent called after this validation
                            String requestURI = request.getRequestURI();
                            if (requestURI.endsWith("/" + dbId))
                                return;
                        }
                        errors.reject(EXCEPTION_DUPLICATE_NAME_AND_ADDRESS);
                    });
        }
    }
}
