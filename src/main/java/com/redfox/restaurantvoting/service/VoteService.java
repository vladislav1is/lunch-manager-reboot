package com.redfox.restaurantvoting.service;

import com.redfox.restaurantvoting.error.vote.AlreadyVotedException;
import com.redfox.restaurantvoting.error.vote.DeadlineException;
import com.redfox.restaurantvoting.error.vote.NotVotedException;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.model.Vote;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.VoteRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository repository;
    private final RestaurantRepository restaurantRepository;

    @Setter
    @Getter
    @Value("${app.deadline}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime deadline;

    @Transactional
    public Vote createToday(User user, int restaurantId) {
        LocalDateTime now = LocalDateTime.now();
        Optional<Vote> dbVote = repository.findByDateAndUserId(now.toLocalDate(), user.id());
        dbVote.ifPresent(v -> {
            throw new AlreadyVotedException("Already voted today");
        });
        restaurantRepository.checkAvailable(restaurantId);
        Vote vote = new Vote(null, user, now.toLocalDate(), now.toLocalTime().truncatedTo(ChronoUnit.SECONDS), restaurantId);
        return repository.save(vote);
    }

    @Transactional
    public void updateToday(User user, int restaurantId) {
        LocalDateTime now = LocalDateTime.now();
        Vote vote = findVote(user, now);
        restaurantRepository.checkAvailable(restaurantId);
        vote.setActualTime(now.toLocalTime().truncatedTo(ChronoUnit.SECONDS));
        vote.setRestaurantId(restaurantId);
    }

    private Vote findVote(User user, LocalDateTime now) {
        checkDeadline(now.toLocalTime(), deadline);
        Optional<Vote> dbVote = repository.findByDateAndUserId(now.toLocalDate(), user.id());
        return dbVote.orElseThrow(() -> new NotVotedException("Have not voted today"));
    }

    public static void checkDeadline(LocalTime now, LocalTime deadline) {
        if (now.isAfter(deadline)) {
            throw new DeadlineException("Deadline for change vote has passed");
        }
    }

    @Transactional
    public void deleteVote(User user) {
        LocalDateTime now = LocalDateTime.now();
        Vote vote = findVote(user, now);
        repository.delete(vote.id());
    }
}
