package com.lhind.annualleavemanagement.leave.mapper;

import javax.validation.constraints.NotNull;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;

@Mapper(config = MapperConfig.class)
public abstract class LeaveMapper {

    @Mapping(target = "user", ignore = true)
    public abstract LeaveDto toDto(LeaveEntity entity);

    public abstract LeaveEntity toEntity(LeaveDto dto);

    public abstract LeaveEntity updateFromDto(LeaveDto dto, @MappingTarget @NotNull LeaveEntity leaveEntity);
}
