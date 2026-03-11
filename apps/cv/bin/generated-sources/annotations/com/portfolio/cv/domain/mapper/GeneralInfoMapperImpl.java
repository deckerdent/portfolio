package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.GeneralInfo;
import com.portfolio.cv.model.GeneralInfoRequest;
import com.portfolio.cv.model.GeneralInfoResponse;
import com.portfolio.cv.model.MaritalStatus;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-11T16:42:43+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class GeneralInfoMapperImpl implements GeneralInfoMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public GeneralInfoResponse toDto(GeneralInfo entity) {
        if ( entity == null ) {
            return null;
        }

        GeneralInfoResponse generalInfoResponse = new GeneralInfoResponse();

        generalInfoResponse.setId( entity.getId() );
        generalInfoResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        generalInfoResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        generalInfoResponse.setFirstName( entity.getFirstName() );
        generalInfoResponse.setLastName( entity.getLastName() );
        generalInfoResponse.setMaritalStatus( maritalStatusToMaritalStatus( entity.getMaritalStatus() ) );
        generalInfoResponse.setNumberOfChildren( entity.getNumberOfChildren() );
        generalInfoResponse.setDateOfBirth( entity.getDateOfBirth() );
        generalInfoResponse.setPlaceOfBirth( entity.getPlaceOfBirth() );
        generalInfoResponse.setNationality( entity.getNationality() );
        generalInfoResponse.setImageUrl( entity.getImageUrl() );
        generalInfoResponse.setSummary( entity.getSummary() );

        return generalInfoResponse;
    }

    @Override
    public GeneralInfo toEntity(GeneralInfoRequest request) {
        if ( request == null ) {
            return null;
        }

        GeneralInfo.GeneralInfoBuilder generalInfo = GeneralInfo.builder();

        generalInfo.dateOfBirth( request.getDateOfBirth() );
        generalInfo.firstName( request.getFirstName() );
        generalInfo.imageUrl( request.getImageUrl() );
        generalInfo.lastName( request.getLastName() );
        generalInfo.maritalStatus( maritalStatusToMaritalStatus1( request.getMaritalStatus() ) );
        generalInfo.nationality( request.getNationality() );
        generalInfo.numberOfChildren( request.getNumberOfChildren() );
        generalInfo.placeOfBirth( request.getPlaceOfBirth() );
        generalInfo.summary( request.getSummary() );

        return generalInfo.build();
    }

    protected MaritalStatus maritalStatusToMaritalStatus(GeneralInfo.MaritalStatus maritalStatus) {
        if ( maritalStatus == null ) {
            return null;
        }

        MaritalStatus maritalStatus1;

        switch ( maritalStatus ) {
            case SINGLE: maritalStatus1 = MaritalStatus.SINGLE;
            break;
            case MARRIED: maritalStatus1 = MaritalStatus.MARRIED;
            break;
            case DIVORCED: maritalStatus1 = MaritalStatus.DIVORCED;
            break;
            case WIDOWED: maritalStatus1 = MaritalStatus.WIDOWED;
            break;
            case SEPARATED: maritalStatus1 = MaritalStatus.SEPARATED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + maritalStatus );
        }

        return maritalStatus1;
    }

    protected GeneralInfo.MaritalStatus maritalStatusToMaritalStatus1(MaritalStatus maritalStatus) {
        if ( maritalStatus == null ) {
            return null;
        }

        GeneralInfo.MaritalStatus maritalStatus1;

        switch ( maritalStatus ) {
            case SINGLE: maritalStatus1 = GeneralInfo.MaritalStatus.SINGLE;
            break;
            case MARRIED: maritalStatus1 = GeneralInfo.MaritalStatus.MARRIED;
            break;
            case DIVORCED: maritalStatus1 = GeneralInfo.MaritalStatus.DIVORCED;
            break;
            case WIDOWED: maritalStatus1 = GeneralInfo.MaritalStatus.WIDOWED;
            break;
            case SEPARATED: maritalStatus1 = GeneralInfo.MaritalStatus.SEPARATED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + maritalStatus );
        }

        return maritalStatus1;
    }
}
