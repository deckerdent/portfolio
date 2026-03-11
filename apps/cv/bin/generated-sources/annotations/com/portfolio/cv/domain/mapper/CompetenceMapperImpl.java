package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Competence;
import com.portfolio.cv.model.CompetenceRequest;
import com.portfolio.cv.model.CompetenceResponse;
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
public class CompetenceMapperImpl implements CompetenceMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CompetenceResponse toDto(Competence entity) {
        if ( entity == null ) {
            return null;
        }

        CompetenceResponse competenceResponse = new CompetenceResponse();

        competenceResponse.setId( entity.getId() );
        competenceResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        competenceResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        competenceResponse.setDescription( entity.getDescription() );

        return competenceResponse;
    }

    @Override
    public Competence toEntity(CompetenceRequest request) {
        if ( request == null ) {
            return null;
        }

        Competence.CompetenceBuilder competence = Competence.builder();

        competence.description( request.getDescription() );

        return competence.build();
    }

    @Override
    public List<CompetenceResponse> toDtoList(List<Competence> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CompetenceResponse> list = new ArrayList<CompetenceResponse>( entities.size() );
        for ( Competence competence : entities ) {
            list.add( toDto( competence ) );
        }

        return list;
    }
}
