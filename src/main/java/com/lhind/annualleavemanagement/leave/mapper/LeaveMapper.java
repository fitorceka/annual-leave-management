package com.lhind.annualleavemanagement.leave.mapper;

import com.lhind.annualleavemanagement.config.MapperConfig;
import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.validation.constraints.NotNull;
import java.util.List;

@Mapper(config = MapperConfig.class)
public abstract class LeaveMapper {

    @Mapping(target = "user.leaves", source = "entity.user.leaves")
    public abstract LeaveDto toDto(LeaveEntity entity);

    @Mapping(target = "user.leaves", source = "entities.user.leaves")
    public abstract List<LeaveDto> toDtos(List<LeaveEntity> entities);

    @Mapping(target = "user.leaves", source = "dto.user.leaves")
    public abstract LeaveEntity toEntity(LeaveDto dto);

    @Mapping(target = "user.leaves", source = "dto.user.leaves")
    public abstract LeaveEntity updateFromDto(LeaveDto dto, @MappingTarget @NotNull LeaveEntity leaveEntity);
}
