package com.redfox.restaurantvoting.mapper;

import com.redfox.restaurantvoting.model.BaseEntity;
import com.redfox.restaurantvoting.to.BaseTo;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

public interface BaseMapper<E extends BaseEntity, T extends BaseTo> {

    E toEntity(T to);

    List<E> toEntityList(Collection<T> tos);

    E updateFromTo(@MappingTarget E entity, T to);

    T toTo(E entity);

    List<T> toToList(Collection<E> entities);
}
