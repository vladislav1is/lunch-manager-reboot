package com.redfox.restaurantvoting.mapper;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.to.RestaurantWithVote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantWithVoteMapper extends BaseMapper<Restaurant, RestaurantWithVote> {
}
