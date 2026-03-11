package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Education;
import com.portfolio.cv.model.EducationRequest;
import com.portfolio.cv.model.EducationResponse;
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
public class EducationMapperImpl implements EducationMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public EducationResponse toDto(Education entity) {
        if ( entity == null ) {
            return null;
        }

        EducationResponse educationResponse = new EducationResponse();

        educationResponse.setId( entity.getId() );
        educationResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        educationResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        educationResponse.setTitle( entity.getTitle() );
        educationResponse.setSchoolName( entity.getSchoolName() );
        educationResponse.setStartDate( entity.getStartDate() );
        educationResponse.setEndDate( dateMapper.toJsonNullable( entity.getEndDate() ) );
        educationResponse.setLocation( entity.getLocation() );
        educationResponse.setDescription( entity.getDescription() );

        return educationResponse;
    }

    @Override
    public Education toEntity(EducationRequest request) {
        if ( request == null ) {
            return null;
        }

        Education.EducationBuilder education = Education.builder();

        education.schoolName( request.getSchoolName() );

        return education.build();
    }

    @Override
    public List<EducationResponse> toDtoList(List<Education> entities) {
        if ( entities == null ) {
            return null;
        }

        List<EducationResponse> list = new ArrayList<EducationResponse>( entities.size() );
        for ( Education education : entities ) {
            list.add( toDto( education ) );
        }

        return list;
    }
}
