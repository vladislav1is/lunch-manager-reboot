package com.redfox.restaurantvoting.web.vote;

import com.redfox.restaurantvoting.model.Vote;
import com.redfox.restaurantvoting.repository.VoteRepository;
import com.redfox.restaurantvoting.service.VoteService;
import com.redfox.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.DODO_ID;
import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.YAKITORIYA_ID;
import static com.redfox.restaurantvoting.web.user.UserTestData.*;
import static com.redfox.restaurantvoting.web.vote.VoteTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends AbstractControllerTest {
    private static final String REST_URL = VoteController.REST_URL + '/';

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteService voteService;

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getOwn() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(vote_1, vote_2, vote_4));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getOwnForToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(vote_1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getOwnByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", "2022-04-13"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(vote_5));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void voteToday() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", Integer.toString(DODO_ID))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Vote created = VOTE_MATCHER.readFromJson(action);
        LocalDate date = created.getActualDate();
        voteRepository.findByDateAndUserId(date, ADMIN_ID).ifPresent(vote -> VOTE_MATCHER.assertMatch(created, vote));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void revoteTodayBeforeDeadline() throws Exception {
        voteService.setDeadline(LocalTime.MAX);
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurantId", Integer.toString(YAKITORIYA_ID))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        voteRepository.findByDateAndUserId(LocalDate.now(), USER_ID).ifPresent(vote -> {
            assertEquals(vote.getRestaurantId(), YAKITORIYA_ID);
            assertEquals(vote.getActualTime(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        });
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void revoteTodayAfterDeadline() throws Exception {
        voteService.setDeadline(LocalTime.MIN);
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurantId", Integer.toString(YAKITORIYA_ID))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteTodayBeforeDeadline() throws Exception {
        voteService.setDeadline(LocalTime.MAX);
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andExpect(status().isNoContent());
        assertFalse(voteRepository.findByDateAndUserId(LocalDate.now(), USER_ID).isPresent());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteTodayAfterDeadline() throws Exception {
        voteService.setDeadline(LocalTime.MIN);
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andDo(print())
                .andExpect(status().isConflict());
    }
}