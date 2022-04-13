package com.redfox.restaurantvoting;

import com.redfox.restaurantvoting.model.*;
import com.redfox.restaurantvoting.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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
        User user = userRepository.save(new User(null, "User_First", "user@gmail.com", "password", Role.USER));
        userRepository.save(new User(null, "Admin_First", "admin@javaops.ru", "admin", Role.USER, Role.ADMIN));
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(userRepository.findAll());

        System.out.println("===========================================Restaurant=========================================");
        Restaurant restaurant = restaurantRepository.save(new Restaurant(null, "Mc", null, null));
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(restaurantRepository.findAll());

        System.out.println("===========================================DishRef============================================");
        DishRef dishRef = new DishRef(null, "burger", 100_00, restaurant);
        dishRefRepository.save(dishRef);
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(dishRefRepository.findAll());

        System.out.println("===========================================MenuItem===========================================");
        MenuItem menuItem = menuItemRepository.save(new MenuItem(null, LocalDate.now(), restaurant, dishRef));
        System.out.println(restaurant + " / " + dishRef);
        System.out.println(menuItem);
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(menuItemRepository.findAll());

        System.out.println("===========================================Vote===============================================");
        voteRepository.save(new Vote(null, user, LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.MINUTES), restaurant));
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(voteRepository.findAll());
    }
}
