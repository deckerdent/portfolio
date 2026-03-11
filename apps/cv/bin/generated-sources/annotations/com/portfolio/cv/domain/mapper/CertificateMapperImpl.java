package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Certificate;
import com.portfolio.cv.model.CertificateRequest;
import com.portfolio.cv.model.CertificateResponse;
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
public class CertificateMapperImpl implements CertificateMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CertificateResponse toDto(Certificate entity) {
        if ( entity == null ) {
            return null;
        }

        CertificateResponse certificateResponse = new CertificateResponse();

        certificateResponse.setId( entity.getId() );
        certificateResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        certificateResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        certificateResponse.setTitle( entity.getTitle() );
        certificateResponse.setIssuingOrganization( entity.getIssuingOrganization() );
        certificateResponse.setStartDate( entity.getStartDate() );
        certificateResponse.setEndDate( dateMapper.toJsonNullable( entity.getEndDate() ) );
        certificateResponse.setLocation( entity.getLocation() );
        certificateResponse.setDescription( entity.getDescription() );

        return certificateResponse;
    }

    @Override
    public Certificate toEntity(CertificateRequest request) {
        if ( request == null ) {
            return null;
        }

        Certificate.CertificateBuilder certificate = Certificate.builder();

        certificate.issuingOrganization( request.getIssuingOrganization() );

        return certificate.build();
    }

    @Override
    public List<CertificateResponse> toDtoList(List<Certificate> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CertificateResponse> list = new ArrayList<CertificateResponse>( entities.size() );
        for ( Certificate certificate : entities ) {
            list.add( toDto( certificate ) );
        }

        return list;
    }
}
