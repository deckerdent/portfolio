package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Language;
import com.portfolio.cv.model.LanguageRequest;
import com.portfolio.cv.model.LanguageResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-11T16:42:43+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class LanguageMapperImpl implements LanguageMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public LanguageResponse toDto(Language entity) {
        if ( entity == null ) {
            return null;
        }

        LanguageResponse languageResponse = new LanguageResponse();

        languageResponse.setId( entity.getId() );
        languageResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        languageResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        languageResponse.setName( entity.getName() );
        languageResponse.setLevel( entity.getLevel() );

        return languageResponse;
    }

    @Override
    public Language toEntity(LanguageRequest request) {
        if ( request == null ) {
            return null;
        }

        Language.LanguageBuilder language = Language.builder();

        language.level( request.getLevel() );
        language.name( request.getName() );

        return language.build();
    }

    @Override
    public List<LanguageResponse> toDtoList(List<Language> entities) {
        if ( entities == null ) {
            return null;
        }

        List<LanguageResponse> list = new ArrayList<LanguageResponse>( entities.size() );
        for ( Language language : entities ) {
            list.add( toDto( language ) );
        }

        return list;
    }
}
