package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Skill;
import com.portfolio.cv.model.SkillRequest;
import com.portfolio.cv.model.SkillResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface SkillMapper {

    SkillResponse toDto(Skill entity);

    Skill toEntity(SkillRequest request);

    List<SkillResponse> toDtoList(List<Skill> entities);
}
