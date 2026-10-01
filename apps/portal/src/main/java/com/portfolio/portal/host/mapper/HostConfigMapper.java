package com.portfolio.portal.host.mapper;

import com.portfolio.portal.host.model.ConfigProp;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper converting the JPA {@link ConfigProp} entity to the
 * OpenAPI-generated {@code com.portfolio.portal.model.ConfigPropsDto} DTO.
 *
 * <p>
 * The fully-qualified target type avoids any potential name clash between the
 * entity and the generated model class.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface HostConfigMapper {

    com.portfolio.portal.model.ConfigPropsDto toDto(ConfigProp entity);

    List<com.portfolio.portal.model.ConfigPropsDto> toDtoList(List<ConfigProp> entities);
}
