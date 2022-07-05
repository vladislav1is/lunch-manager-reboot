package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.util.JsonUtil;
import com.redfox.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.redfox.restaurantvoting.web.restaurant.RestaurantTestData.*;
import static com.redfox.restaurantvoting.web.user.UserTestData.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminDishRefControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestaurantController.REST_URL + '/';

    @Autowired
    private DishRefRepository dishRefRepository;

    private String getUrl(int restaurantId) {
        return REST_URL + restaurantId + "/dish-refs";
    }

    private String getUrl(int restaurantId, int id) {
        return getUrl(restaurantId) + "/" + id;
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID, yakitoriya_rsc.id())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = R_ADMIN_MAIL)
    void getNotBelong() throws Exception {
        String servletPath = getUrl(MAC_ID);
        perform(MockMvcRequestBuilders.get(servletPath)
                .servletPath(servletPath))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID, yakitoriya_rsc.id())))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_REF_MATCHER.contentJson(yakitoriya_rsc));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(MAC_ID, yakitoriya_rsc.id())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(getUrl(DODO_ID, dodo_c.id())))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(dishRefRepository.findById(dodo_c.id()).isPresent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        DishRef updated = getUpdatedDish();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(getUrl(YAKITORIYA_ID, yakitoriya_c.id()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        DISH_REF_MATCHER.assertMatch(dishRefRepository.getById(yakitoriya_c.id()), getUpdatedDish());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        DishRef newDishRef = getNewDish();
        ResultActions action = perform(MockMvcRequestBuilders.post(getUrl(YAKITORIYA_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDishRef)))
                .andExpect(status().isCreated());

        DishRef created = DISH_REF_MATCHER.readFromJson(action);
        int newId = created.id();
        newDishRef.setId(newId);
        DISH_REF_MATCHER.assertMatch(created, newDishRef);
        DISH_REF_MATCHER.assertMatch(dishRefRepository.getById(newId), newDishRef);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(getUrl(YAKITORIYA_ID)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_REF_MATCHER.contentJson(yakitoriya_c, yakitoriya_rsc));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(getUrl(MAC_ID, mac_dc.id()))
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(dishRefRepository.getById(mac_dc.id()).isEnabled());
    }
}