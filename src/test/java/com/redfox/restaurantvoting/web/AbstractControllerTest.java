package com.redfox.restaurantvoting.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

// https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
// https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications-testing-with-mock-environment
@Slf4j
public abstract class AbstractControllerTest {
    @BeforeAll
    static void checkCloseMidnight() {
        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.MAX.minus(5, ChronoUnit.MINUTES)) ||
                now.isBefore(LocalTime.MIN.plus(5, ChronoUnit.MINUTES))) {
            log.error("+++ Close to midnight dates in DB and test data and re-voting (and tests result) might be wrong!");
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }
}
