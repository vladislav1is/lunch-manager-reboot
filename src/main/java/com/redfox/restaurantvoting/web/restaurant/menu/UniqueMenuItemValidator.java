package com.redfox.restaurantvoting.web.restaurant.menu;

import com.redfox.restaurantvoting.HasMenuItemConstraint;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Objects;

import static com.redfox.restaurantvoting.web.GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH_REF_AND_DATE;

@Component
@AllArgsConstructor
public class UniqueMenuItemValidator implements org.springframework.validation.Validator {

    private final MenuItemRepository repository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return HasMenuItemConstraint.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        HasMenuItemConstraint menuItem = ((HasMenuItemConstraint) target);

        String path = request.getServletPath();
        int restaurantId = Integer.parseInt(path.split("/")[4]);

        Integer dishRefId = menuItem.getDishRefId();
        LocalDate actualDate = menuItem.getActualDate();
        if (Objects.nonNull(dishRefId) && Objects.nonNull(actualDate)) {
            repository.findByDateAndDishRefId(restaurantId, actualDate, dishRefId)
                    .ifPresent(dbMenuItem -> {
                        if (request.getMethod().equals("PUT") || request.getMethod().equals("POST")) {  // UPDATE
                            int dbId = dbMenuItem.id();

                            // it is ok, if update ourself
                            if (menuItem.getId() != null && dbId == menuItem.id()) return;

                            // Workaround for update with menuItem.id=null in request body
                            // ValidationUtil.assureIdConsistent called after this validation
                            String requestURI = request.getRequestURI();
                            if (requestURI.endsWith("/" + dbId))
                                return;
                        }
                        errors.reject(EXCEPTION_DUPLICATE_DISH_REF_AND_DATE);
                    });
        }
    }
}
