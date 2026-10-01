package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Competence;
import com.portfolio.cv.model.CompetenceRequest;
import com.portfolio.cv.model.CompetenceResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface CompetenceMapper {

    CompetenceResponse toDto(Competence entity);

    Competence toEntity(CompetenceRequest request);

    List<CompetenceResponse> toDtoList(List<Competence> entities);
}
