package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.ProfessionalExperience;
import com.portfolio.cv.model.ExperienceRequest;
import com.portfolio.cv.model.ExperienceResponse;
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
public class ExperienceMapperImpl implements ExperienceMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public ExperienceResponse toDto(ProfessionalExperience entity) {
        if ( entity == null ) {
            return null;
        }

        ExperienceResponse experienceResponse = new ExperienceResponse();

        experienceResponse.setId( entity.getId() );
        experienceResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        experienceResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        experienceResponse.setTitle( entity.getTitle() );
        experienceResponse.setCompanyName( entity.getCompanyName() );
        experienceResponse.setStartDate( entity.getStartDate() );
        experienceResponse.setEndDate( dateMapper.toJsonNullable( entity.getEndDate() ) );
        experienceResponse.setLocation( entity.getLocation() );
        experienceResponse.setDescription( entity.getDescription() );

        return experienceResponse;
    }

    @Override
    public ProfessionalExperience toEntity(ExperienceRequest request) {
        if ( request == null ) {
            return null;
        }

        ProfessionalExperience.ProfessionalExperienceBuilder professionalExperience = ProfessionalExperience.builder();

        professionalExperience.companyName( request.getCompanyName() );

        return professionalExperience.build();
    }

    @Override
    public List<ExperienceResponse> toDtoList(List<ProfessionalExperience> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ExperienceResponse> list = new ArrayList<ExperienceResponse>( entities.size() );
        for ( ProfessionalExperience professionalExperience : entities ) {
            list.add( toDto( professionalExperience ) );
        }

        return list;
    }
}
