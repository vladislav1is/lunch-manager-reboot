package com.redfox.restaurantvoting.web.restaurant.menu;

import com.redfox.restaurantvoting.HasMenuItemToConstraint;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import static com.redfox.restaurantvoting.web.GlobalExceptionHandler.EXCEPTION_DUPLICATE_NAME_AND_DATE;

@Component
@AllArgsConstructor
public class UniqueMenuItemToValidator implements org.springframework.validation.Validator {

    private final MenuItemRepository repository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return HasMenuItemToConstraint.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        HasMenuItemToConstraint menuItem = ((HasMenuItemToConstraint) target);

        String path = request.getServletPath();
        int restaurantId = Integer.parseInt(path.split("/")[3]);

        String name = menuItem.getName().strip();
        String date = menuItem.getActualDate().strip();
        if (StringUtils.hasText(name) && StringUtils.hasText(date)) {
            repository.findByNameDateAndRestaurantId(restaurantId, LocalDate.parse(date), name)
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
                        errors.reject(EXCEPTION_DUPLICATE_NAME_AND_DATE);
                    });
        }
    }
}
