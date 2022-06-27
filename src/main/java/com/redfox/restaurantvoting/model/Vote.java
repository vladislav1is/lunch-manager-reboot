package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redfox.restaurantvoting.util.DateTimeUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "vote", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "actual_date"}, name = "vote_uk")})
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = "user")
public class Vote extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @JsonIgnore
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    @Column(name = "actual_date", nullable = false)
    @NotNull
    @JsonFormat(pattern= DateTimeUtil.DATE_PATTERN)
    private LocalDate actualDate;

    @Column(name = "actual_time", nullable = false)
    @NotNull
    @JsonFormat(pattern= DateTimeUtil.TIME_PATTERN)
    private LocalTime actualTime;

    @Column(name = "restaurant_id")
    private int restaurantId;

    public Vote(Integer id, @NotNull User user, @NotNull LocalDate actualDate, @NotNull LocalTime actualTime, int restaurantId) {
        super(id);
        this.user = user;
        this.userId = user.id();
        this.actualDate = actualDate;
        this.actualTime = actualTime;
        this.restaurantId = restaurantId;
    }
}
