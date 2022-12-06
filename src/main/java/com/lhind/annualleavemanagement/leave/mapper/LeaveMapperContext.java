package com.lhind.annualleavemanagement.leave.mapper;

import java.util.IdentityHashMap;
import java.util.Map;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;

public class LeaveMapperContext {
    private final Map<LeaveEntity, LeaveDto> knownInstances = new IdentityHashMap<>();

    @BeforeMapping
    public <T> T getMappedInstance(LeaveEntity source, @TargetType Class<T> targetType) {
        return (T) knownInstances.get(source);
    }

    @BeforeMapping
    public void storeMappedInstance(LeaveEntity source, @MappingTarget LeaveDto target) {
        knownInstances.put(source, target);
    }
}
