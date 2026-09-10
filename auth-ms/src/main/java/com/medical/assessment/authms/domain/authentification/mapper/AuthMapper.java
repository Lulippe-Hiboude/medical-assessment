package com.medical.assessment.authms.domain.authentification.mapper;

import com.medical.assessment.authms.authentification.model.AuthResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "token", source = "token")
    AuthResponse toAuthResponse(final String token);
}
