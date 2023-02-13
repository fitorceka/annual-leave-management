package com.lhind.annualleavemanagement.leave.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.util.enums.LeaveReason;

@Mapper(config = MapperConfig.class)
public abstract class LeaveMapper {

    public abstract LeaveDto toDto(LeaveEntity entity, @Context LeaveMapperContext context);

    @Mapping(target = "user.authorities", ignore = true)
    @Mapping(target = "leaveReason", source = "dto", qualifiedByName = "mapLeaveReason")
    public abstract LeaveEntity toEntity(LeaveDto dto, @Context LeaveMapperContext context);

    @Named("mapLeaveReason")
    LeaveReason mapLeaveReason(LeaveDto dto) {
        return LeaveReason.fromValue(dto.getLeaveReason());
    }
}
