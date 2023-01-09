package com.lhind.annualleavemanagement.user.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Mapper(config = MapperConfig.class)
public abstract class UserMapper {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mapping(target = "password", ignore = true)
    public abstract UserDto toDto(UserEntity entity, @Context UserMapperContext context);

    @Mapping(target = "password", source = "dto", qualifiedByName = "encodePassword")
    public abstract UserEntity toEntity(UserDto dto, @Context UserMapperContext context);

    @Named("encodePassword")
    String encodePassword(UserDto dto) {
        return bCryptPasswordEncoder.encode(dto.getPassword());
    }
}
