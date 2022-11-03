package com.lhind.annualleavemanagement.config;

import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.MapperConfig(componentModel = "spring",
                            nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
                            unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapperConfig {

}
