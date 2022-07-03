package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.model.Vote;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.VoteRepository;
import com.redfox.restaurantvoting.service.VoteService;
import com.redfox.restaurantvoting.to.RestaurantWithVisitors;
import com.redfox.restaurantvoting.to.RestaurantWithVote;
import com.redfox.restaurantvoting.util.JsonUtil;
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

import static com.redfox.restaurantvoting.service.VoteService.checkDeadline;

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

    @GetMapping
    public List<RestaurantWithVote> getAllEnabledWithUserVote(@AuthenticationPrincipal AuthUser authUser) {
        return super.getAllEnabledWithUserVote(authUser.id());
    }

    @Transactional
    @GetMapping("/{id}")
    public String getWithVisitors(@PathVariable int id) {
        log.info("getWithVisitors for id={}", id);
        Restaurant restaurant = restaurantRepository.getExisted(id);
        restaurantRepository.checkAvailable(id);
        int votesCount = voteRepository.countByDateAndRestaurantId(LocalDate.now(), id);
        return JsonUtil.writeValue(List.of(new RestaurantWithVisitors(restaurant.id(), restaurant.getName(), restaurant.getAddress(), votesCount)));
    }

    @Transactional
    @PostMapping("/{id}/vote")
    public void voteToday(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("voteToday for restaurantId={} and userId={}", id, authUser.id());
        restaurantRepository.checkAvailable(id);
        LocalDateTime now = LocalDateTime.now();
        Optional<Vote> dbVote = voteRepository.findByDateAndUserId(LocalDate.now(), authUser.id());
        if (dbVote.isPresent()) {
            checkDeadline(now.toLocalTime(), voteService.getDeadline());
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

    @GetMapping("/{id}/count-visitors-today")
    public ResponseEntity<String> countVisitorsForToday(@PathVariable int id) {
        return ResponseEntity.ok(super.countVisitorsByRestaurantAndDate(id, LocalDate.now()));
    }
}