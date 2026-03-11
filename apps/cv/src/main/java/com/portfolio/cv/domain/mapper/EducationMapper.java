package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Education;
import com.portfolio.cv.model.EducationRequest;
import com.portfolio.cv.model.EducationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface EducationMapper {

    EducationResponse toDto(Education entity);

    Education toEntity(EducationRequest request);

    List<EducationResponse> toDtoList(List<Education> entities);
}
