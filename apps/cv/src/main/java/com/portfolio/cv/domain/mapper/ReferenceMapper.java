package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Reference;
import com.portfolio.cv.model.ReferenceRequest;
import com.portfolio.cv.model.ReferenceResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface ReferenceMapper {

    ReferenceResponse toDto(Reference entity);

    Reference toEntity(ReferenceRequest request);

    List<ReferenceResponse> toDtoList(List<Reference> entities);
}
