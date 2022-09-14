package com.lhind.annualleavemanagement.user.mapper;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.validation.constraints.NotNull;
import java.util.List;

@Mapper(config = MapperConfig.class)
public abstract class UserMapper {

    @Mapping(ignore = true, target = "password")
    public abstract UserDto toDto(UserEntity entity);

    @Mapping(ignore = true, target = "password")
    public abstract List<UserDto> toDtos(List<UserEntity> entities);

    public abstract UserEntity toEntity(UserDto dto);

    public abstract UserEntity updateFromEntity(UserDto dto, @MappingTarget @NotNull UserEntity entity);
}
