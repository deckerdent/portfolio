package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Reference;
import com.portfolio.cv.model.ReferenceRelation;
import com.portfolio.cv.model.ReferenceRequest;
import com.portfolio.cv.model.ReferenceResponse;
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
public class ReferenceMapperImpl implements ReferenceMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public ReferenceResponse toDto(Reference entity) {
        if ( entity == null ) {
            return null;
        }

        ReferenceResponse referenceResponse = new ReferenceResponse();

        referenceResponse.setId( entity.getId() );
        referenceResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        referenceResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        referenceResponse.setFirstName( entity.getFirstName() );
        referenceResponse.setLastName( entity.getLastName() );
        referenceResponse.setDescription( entity.getDescription() );
        referenceResponse.setRelation( referenceRelationToReferenceRelation( entity.getRelation() ) );

        return referenceResponse;
    }

    @Override
    public Reference toEntity(ReferenceRequest request) {
        if ( request == null ) {
            return null;
        }

        Reference.ReferenceBuilder reference = Reference.builder();

        reference.description( request.getDescription() );
        reference.firstName( request.getFirstName() );
        reference.lastName( request.getLastName() );
        reference.relation( referenceRelationToReferenceRelation1( request.getRelation() ) );

        return reference.build();
    }

    @Override
    public List<ReferenceResponse> toDtoList(List<Reference> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ReferenceResponse> list = new ArrayList<ReferenceResponse>( entities.size() );
        for ( Reference reference : entities ) {
            list.add( toDto( reference ) );
        }

        return list;
    }

    protected ReferenceRelation referenceRelationToReferenceRelation(Reference.ReferenceRelation referenceRelation) {
        if ( referenceRelation == null ) {
            return null;
        }

        ReferenceRelation referenceRelation1;

        switch ( referenceRelation ) {
            case COWORKER: referenceRelation1 = ReferenceRelation.COWORKER;
            break;
            case MANAGER: referenceRelation1 = ReferenceRelation.MANAGER;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + referenceRelation );
        }

        return referenceRelation1;
    }

    protected Reference.ReferenceRelation referenceRelationToReferenceRelation1(ReferenceRelation referenceRelation) {
        if ( referenceRelation == null ) {
            return null;
        }

        Reference.ReferenceRelation referenceRelation1;

        switch ( referenceRelation ) {
            case COWORKER: referenceRelation1 = Reference.ReferenceRelation.COWORKER;
            break;
            case MANAGER: referenceRelation1 = Reference.ReferenceRelation.MANAGER;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + referenceRelation );
        }

        return referenceRelation1;
    }
}
