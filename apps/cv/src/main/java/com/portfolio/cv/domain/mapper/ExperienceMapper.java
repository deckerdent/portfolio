package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.ProfessionalExperience;
import com.portfolio.cv.model.ExperienceRequest;
import com.portfolio.cv.model.ExperienceResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class, builder = @Builder(disableBuilder = true))
public interface ExperienceMapper {

    ExperienceResponse toDto(ProfessionalExperience entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProfessionalExperience toEntity(ExperienceRequest request);

    List<ExperienceResponse> toDtoList(List<ProfessionalExperience> entities);
}
