package com.redfox.restaurantvoting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfox.restaurantvoting.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
@Slf4j
@EnableCaching
public class AppConfig {

    @Autowired
    public void storeObjectMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }

    //  https://stackoverflow.com/questions/37068808/how-to-start-h2-tcp-server-on-spring-boot-application-startup#45643148
    @Bean(initMethod = "start", destroyMethod = "stop")
    Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
}