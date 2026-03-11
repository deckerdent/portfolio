package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Language;
import com.portfolio.cv.model.LanguageRequest;
import com.portfolio.cv.model.LanguageResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface LanguageMapper {

    LanguageResponse toDto(Language entity);

    Language toEntity(LanguageRequest request);

    List<LanguageResponse> toDtoList(List<Language> entities);
}
