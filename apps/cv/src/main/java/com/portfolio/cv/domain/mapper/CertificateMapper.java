package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Certificate;
import com.portfolio.cv.model.CertificateRequest;
import com.portfolio.cv.model.CertificateResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface CertificateMapper {

    CertificateResponse toDto(Certificate entity);

    Certificate toEntity(CertificateRequest request);

    List<CertificateResponse> toDtoList(List<Certificate> entities);
}
