package com.redfox.restaurantvoting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfox.restaurantvoting.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.SQLException;

@Configuration
@Slf4j
@EnableCaching
@EnableScheduling
@AllArgsConstructor
public class AppConfig {
    private final CacheManager cacheManager;

    @Autowired
    public void storeObjectMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }

    // https://stackoverflow.com/questions/37068808/how-to-start-h2-tcp-server-on-spring-boot-application-startup#45643148
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("dev")
    Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    @Scheduled(cron = "0 11 * * * *") // clear at midnight
    public void clearCacheAtMidnight() {
        log.info("clearCacheAtMidnight");
        cacheManager.getCache("allRestaurantsWithMenu").clear();
        cacheManager.getCache("restaurantWithMenu").clear();
    }
}