package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.UserRepository;
import com.redfox.restaurantvoting.repository.VoteRepository;
import com.redfox.restaurantvoting.util.JsonUtil;
import com.redfox.restaurantvoting.util.validation.AdminRestaurantsUtil;
import com.redfox.restaurantvoting.web.AbstractControllerTest;
import com.redfox.restaurantvoting.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.*;
import static com.redfox.restaurantvoting.web.user.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminRestaurantControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestaurantsUtil.REST_URL + '/';

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = R_ADMIN_MAIL)
    void getNotBelong() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MAC_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + YAKITORIYA_ID))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(yakitoriya));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(mac, starbucks, dodo, teremok, yakitoriya));
    }

    @Test
    @WithUserDetails(value = R_ADMIN_MAIL)
    void getIn() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(dodo, teremok));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        voteRepository.deleteByRestaurantId(MAC_ID);
        menuItemRepository.deleteByRestaurantId(MAC_ID);
        perform(MockMvcRequestBuilders.delete(REST_URL + MAC_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(restaurantRepository.findById(MAC_ID).isPresent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        Restaurant updated = RestaurantTestData.getUpdated();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + YAKITORIYA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        RESTAURANT_MATCHER.assertMatch(restaurantRepository.getById(YAKITORIYA_ID), RestaurantTestData.getUpdated());
    }

    @Test
    @WithUserDetails(value = R_ADMIN_MAIL)
    void createWithLocation() throws Exception {
        Restaurant newRestaurant = RestaurantTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurant)))
                .andExpect(status().isCreated());

        Restaurant created = RESTAURANT_MATCHER.readFromJson(action);
        int newId = created.id();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.getById(newId), newRestaurant);

        Set<Integer> newAdminRestaurants = new HashSet<>(UserTestData.r_admin.getAdminRestaurants());
        newAdminRestaurants.add(newId);
        assertEquals(newAdminRestaurants, userRepository.getById(R_ADMIN_ID).getAdminRestaurants());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + YAKITORIYA_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(restaurantRepository.getById(YAKITORIYA_ID).isEnabled());
    }
}