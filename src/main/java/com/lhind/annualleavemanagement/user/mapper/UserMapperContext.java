package com.lhind.annualleavemanagement.user.mapper;

import java.util.IdentityHashMap;
import java.util.Map;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

public class UserMapperContext {
    private final Map<UserEntity, UserDto> knownInstances = new IdentityHashMap<>();

    @BeforeMapping
    public <T> T getMappedInstance(UserEntity source, @TargetType Class<T> targetType) {
        return (T) knownInstances.get(source);
    }

    @BeforeMapping
    public void storeMappedInstance(UserEntity source, @MappingTarget UserDto target) {
        knownInstances.put(source, target);
    }
}
