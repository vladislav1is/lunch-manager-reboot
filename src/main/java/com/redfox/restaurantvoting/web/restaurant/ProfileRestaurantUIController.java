package com.redfox.restaurantvoting.web.restaurant;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.model.Vote;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.VoteRepository;
import com.redfox.restaurantvoting.service.VoteService;
import com.redfox.restaurantvoting.to.RestaurantWithVisitors;
import com.redfox.restaurantvoting.util.validation.Validations;
import com.redfox.restaurantvoting.web.AuthUser;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Hidden
@RestController
@RequestMapping(value = ProfileRestaurantUIController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class ProfileRestaurantUIController extends AbstractRestaurantController {
    static final String REST_URL = "/profile/restaurants";

    private final RestaurantRepository restaurantRepository;

    private final VoteRepository voteRepository;
    private final VoteService voteService;

    @Override
    @GetMapping
    public List<Restaurant> getAllEnabled() {
        return super.getAllEnabled();
    }

    @Transactional
    @GetMapping("/{id}")
    public List<Restaurant> getRestaurant(@PathVariable int id) {
        log.info("get for restaurantId={}", id);
        Restaurant restaurant = restaurantRepository.getExisted(id);
        restaurantRepository.checkAvailable(id);
        return List.of(restaurant);
    }

    @Transactional
    @PostMapping("/{id}/vote")
    public void voteToday(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("voteToday for restaurantId={} and userId={}", id, authUser.id());
        restaurantRepository.checkAvailable(id);
        LocalDateTime now = LocalDateTime.now();
        Optional<Vote> dbVote = voteRepository.findByDateAndUserId(LocalDate.now(), authUser.id());
        if (dbVote.isPresent()) {
            Validations.checkDeadline(now.toLocalTime(), voteService.getDeadline());
            Vote vote = dbVote.get();
            vote.setActualTime(now.toLocalTime());
            vote.setRestaurantId(id);
        } else {
            Vote vote = new Vote(null, authUser.getUser(), now.toLocalDate(), now.toLocalTime(), id);
            voteRepository.save(vote);
        }
    }

    @DeleteMapping("/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVoteForToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("deleteVoteForToday for userId={}", authUser.id());
        voteService.deleteVote(authUser.getUser());
    }

    @GetMapping("/{id}/count-visitors")
    @JsonView(View.RestaurantVisitors.class)
    public ResponseEntity<RestaurantWithVisitors> countVisitorsForToday(@PathVariable int id) {
        return ResponseEntity.ok(super.findWithVisitorsByRestaurantAndDate(id, LocalDate.now()));
    }
}