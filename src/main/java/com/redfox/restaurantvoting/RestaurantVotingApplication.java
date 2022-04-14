package com.redfox.restaurantvoting;

import com.redfox.restaurantvoting.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
public class RestaurantVotingApplication implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DishRefRepository dishRefRepository;
    private final VoteRepository voteRepository;

    public static void main(String[] args) {
        SpringApplication.run(RestaurantVotingApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Your application started with option names : {}", args.getOptionNames());
        System.out.println("===========================================User===============================================");
        System.out.println(userRepository.findAll());

        System.out.println("===========================================Restaurant=========================================");
        System.out.println(restaurantRepository.findAll());

        System.out.println("===========================================DishRef============================================");
        System.out.println(dishRefRepository.findAll());

        System.out.println("===========================================MenuItem===========================================");
        System.out.println(menuItemRepository.findAll());

        System.out.println("===========================================Vote===============================================");
        System.out.println(voteRepository.findAll());
    }
}
