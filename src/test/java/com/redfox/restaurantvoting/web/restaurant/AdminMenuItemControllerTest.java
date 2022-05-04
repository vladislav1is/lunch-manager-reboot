package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import com.redfox.restaurantvoting.util.JsonUtil;
import com.redfox.restaurantvoting.util.validation.AdminRestaurantsUtil;
import com.redfox.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.*;
import static com.redfox.restaurantvoting.web.user.UserTestData.*;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMenuItemControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestaurantsUtil.REST_URL + '/';

    @Autowired
    private MenuItemRepository menuItemRepository;

    private String getUrl(int restaurantId) {
        return REST_URL + restaurantId + "/menu-items/";
    }

    private String getUrl(int restaurantId, int id) {
        return getUrl(restaurantId) + id;
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = R_ADMIN_MAIL)
    void getNotBelong() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(MAC_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID, yakitoriya_1.id())))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENUITEM_MATCHER.contentJson(yakitoriya_1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrl(DODO_ID, dodo_3.id())))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(menuItemRepository.getByRestaurantIdAndMenuItem(DODO_ID, dodo_3.id()).isPresent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByRestaurant() throws Exception {
        menuItemRepository.deleteByRestaurantId(YAKITORIYA_ID);
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENUITEM_MATCHER.contentJson(Collections.emptyList()));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByRestaurantAndDate() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID) + "/by-date")
                .param("date", now().format(DateTimeFormatter.ISO_DATE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MENUITEM_MATCHER.contentJson(yakitoriya_1, yakitoriya_2));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getBetweenDatesByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID) + "filter")
                .param("startDate", now().format(DateTimeFormatter.ISO_DATE))
                .param("endDate", now().format(DateTimeFormatter.ISO_DATE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MENUITEM_MATCHER.contentJson(yakitoriya_1, yakitoriya_2));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        MenuItem updated = getUpdatedMenuItem();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(getUrl(YAKITORIYA_ID, yakitoriya_1.id()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        MENUITEM_MATCHER.assertMatch(menuItemRepository.getById(yakitoriya_1.id()), getUpdatedMenuItem());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        MenuItem newMenuItem = getNewMenuItem();
        ResultActions action = perform(MockMvcRequestBuilders.post(getUrl(YAKITORIYA_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMenuItem)))
                .andExpect(status().isCreated());

        MenuItem created = MENUITEM_MATCHER.readFromJson(action);
        int newId = created.id();
        newMenuItem.setId(newId);
        MENUITEM_MATCHER.assertMatch(created, newMenuItem);
        MENUITEM_MATCHER.assertMatch(menuItemRepository.getById(newId), newMenuItem);
    }
}