package com.portfolio.portal.host.mapper;

import com.portfolio.portal.host.model.ConfigProp;
import com.portfolio.portal.model.ConfigPropsDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-11T16:42:49+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class HostConfigMapperImpl implements HostConfigMapper {

    @Override
    public ConfigPropsDto toDto(ConfigProp entity) {
        if ( entity == null ) {
            return null;
        }

        ConfigPropsDto configPropsDto = new ConfigPropsDto();

        configPropsDto.setId( entity.getId() );
        configPropsDto.setKey( entity.getKey() );
        configPropsDto.setValue( entity.getValue() );

        return configPropsDto;
    }

    @Override
    public List<ConfigPropsDto> toDtoList(List<ConfigProp> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ConfigPropsDto> list = new ArrayList<ConfigPropsDto>( entities.size() );
        for ( ConfigProp configProp : entities ) {
            list.add( toDto( configProp ) );
        }

        return list;
    }
}
