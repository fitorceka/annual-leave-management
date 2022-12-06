package com.lhind.annualleavemanagement.user.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Mapper(config = MapperConfig.class)
public abstract class UserMapper {

    @Mapping(ignore = true, target = "password")
    public abstract UserDto toDto(UserEntity entity, @Context UserMapperContext context);

    public abstract UserEntity toEntity(UserDto dto, @Context UserMapperContext context);
}
