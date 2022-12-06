package com.lhind.annualleavemanagement.leave.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;

@Mapper(config = MapperConfig.class)
public abstract class LeaveMapper {

    public abstract LeaveDto toDto(LeaveEntity entity, @Context LeaveMapperContext context);

    public abstract LeaveEntity toEntity(LeaveDto dto, @Context LeaveMapperContext context);
}
