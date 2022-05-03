package com.redfox.restaurantvoting.web.vote;

import com.redfox.restaurantvoting.model.Vote;
import com.redfox.restaurantvoting.web.MatcherFactory;
import com.redfox.restaurantvoting.web.MatcherFactory.Matcher;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.*;
import static com.redfox.restaurantvoting.web.user.UserTestData.admin;
import static com.redfox.restaurantvoting.web.user.UserTestData.user;

public class VoteTestData {
    public static final Matcher<Vote> VOTE_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Vote.class, "user");

    public static final Vote vote_1 = new Vote(1, user, LocalDate.now(), LocalTime.parse("12:35:00"), TEREMOK_ID);
    public static final Vote vote_2 = new Vote(2, user, LocalDate.parse("2022-04-14"), LocalTime.parse("09:13:00"), DODO_ID);
    public static final Vote vote_3 = new Vote(3, admin, LocalDate.parse("2022-04-14"), LocalTime.parse("08:25:00"), DODO_ID);
    public static final Vote vote_4 = new Vote(4, user, LocalDate.parse("2022-04-13"), LocalTime.parse("14:55:00"), MAC_ID);
    public static final Vote vote_5 = new Vote(5, admin, LocalDate.parse("2022-04-13"), LocalTime.parse("12:55:00"), STARBUCKS_ID);
}
