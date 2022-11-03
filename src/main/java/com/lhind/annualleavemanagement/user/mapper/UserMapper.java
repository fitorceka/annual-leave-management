package com.lhind.annualleavemanagement.user.mapper;

import javax.validation.constraints.NotNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.leave.mapper.LeaveMapper;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Mapper(config = MapperConfig.class, uses = LeaveMapper.class)
public abstract class UserMapper {

    @Mapping(ignore = true, target = "password")
    public abstract UserDto toDto(UserEntity entity);

    public abstract UserEntity toEntity(UserDto dto);

    public abstract UserEntity updateFromEntity(UserDto dto, @MappingTarget @NotNull UserEntity entity);
}
