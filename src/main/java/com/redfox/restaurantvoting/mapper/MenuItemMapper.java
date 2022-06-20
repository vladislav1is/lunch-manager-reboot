package com.redfox.restaurantvoting.mapper;

import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.to.MenuItemTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemMapper extends BaseMapper<MenuItem, MenuItemTo> {

    @Override
    @Mapping(target = "dishRef.name", source = "to.name")
    @Mapping(target = "dishRef.price", source = "to.price")
    @Mapping(target = "dishRef.enabled", source = "to.enabled")
    MenuItem toEntity(MenuItemTo to);

    @Override
    @Mapping(target = "name", source = "dishRef.name")
    @Mapping(target = "price", source = "dishRef.price")
    @Mapping(target = "enabled", source = "dishRef.enabled")
    MenuItemTo toTo(MenuItem menuItem);
}
