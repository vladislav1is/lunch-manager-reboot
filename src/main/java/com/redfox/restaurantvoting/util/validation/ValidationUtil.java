package com.redfox.restaurantvoting.util.validation;

import com.redfox.restaurantvoting.error.NotFoundException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil {

    public static void checkModification(int count, int id) {
        if (count == 0) {
            throw new NotFoundException("Entity with id=" + id + " not found");
        }
    }
}