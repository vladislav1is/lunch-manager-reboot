package com.redfox.restaurantvoting.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateTimeUtil {

    private static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(3000, 1, 1);
    private static final LocalDate NOW = LocalDate.now();

    public static LocalDate atDayOrNow(LocalDate localDate) {
        return localDate != null ? localDate : NOW;
    }

    public static LocalDate atDayOrMin(LocalDate localDate) {
        return localDate != null ? localDate : MIN_DATE;
    }

    public static LocalDate atNextDayOrMax(LocalDate localDate) {
        return localDate != null ? localDate.plus(1, ChronoUnit.DAYS) : MAX_DATE;
    }
}
