package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.mapper.RestaurantWithMenuMapper;
import com.redfox.restaurantvoting.mapper.RestaurantWithVoteMapper;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.VoteRepository;
import com.redfox.restaurantvoting.to.RestaurantWithVisitors;
import com.redfox.restaurantvoting.to.RestaurantWithVote;
import com.redfox.restaurantvoting.web.AbstractControllerTest;
import com.redfox.restaurantvoting.web.MatcherFactory;
import com.redfox.restaurantvoting.web.MatcherFactory.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.*;
import static com.redfox.restaurantvoting.web.user.UserTestData.USER_MAIL;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestaurantControllerTest extends AbstractControllerTest {
    private static final String REST_URL = RestaurantController.REST_URL + '/';

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestaurantWithMenuMapper restaurantWithMenuMapper;
    @Autowired
    private RestaurantWithVoteMapper restaurantWithVoteMapper;

    @Test
    void getWithMenuForToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "menu-today"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER_WITH_MENU.contentJson(restaurantWithMenuMapper.toTo(dodo), restaurantWithMenuMapper.toTo(teremok), restaurantWithMenuMapper.toTo(yakitoriya)));
    }

    @Test
    void getWithMenuByRestaurantForToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + YAKITORIYA_ID + "/menu-today"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER_WITH_MENU.contentJson(restaurantWithMenuMapper.toTo(yakitoriya)));
    }

    @Test
    void getDisabled() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MAC_ID + "/menu-today"))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllEnabled() throws Exception {
        restaurantRepository.save(yakitoriya);
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(dodo, teremok, yakitoriya));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllEnabledWithUserVote() throws Exception {
        restaurantRepository.save(yakitoriya);
        List<RestaurantWithVote> restaurantsWithVote = restaurantWithVoteMapper.toToList(List.of(dodo, teremok, yakitoriya));
        RestaurantWithVote restaurantWithVote = restaurantsWithVote.get(1);
        restaurantWithVote.setVoted(true);

        perform(MockMvcRequestBuilders.get(REST_URL + "/vote-today"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER_WITH_VOTE.contentJson(restaurantsWithVote));
    }

    @Test
    void countVisitorsForToday() throws Exception {
        int votesCount = voteRepository.countByDateAndRestaurantId(LocalDate.of(2022, 4, 14), DODO_ID);
        RestaurantWithVisitors restaurantWithVisitors = new RestaurantWithVisitors(dodo.id(), dodo.getName(), dodo.getAddress(), votesCount);
        Matcher<RestaurantWithVisitors> matcher = MatcherFactory.usingIgnoringFieldsComparator(RestaurantWithVisitors.class, "id", "name", "address");

        perform(MockMvcRequestBuilders.get(REST_URL + DODO_ID + "/count-visitors-by-date")
                .param("restaurantId", Integer.toString(DODO_ID))
                .param("date", "2022-04-14"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(matcher.contentJson(restaurantWithVisitors));
    }
}