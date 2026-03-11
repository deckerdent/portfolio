package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.ProfessionalExperience;
import com.portfolio.cv.model.ExperienceRequest;
import com.portfolio.cv.model.ExperienceResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface ExperienceMapper {

    ExperienceResponse toDto(ProfessionalExperience entity);

    ProfessionalExperience toEntity(ExperienceRequest request);

    List<ExperienceResponse> toDtoList(List<ProfessionalExperience> entities);
}
