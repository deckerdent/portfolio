package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Hobby;
import com.portfolio.cv.model.HobbyRequest;
import com.portfolio.cv.model.HobbyResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface HobbyMapper {

    HobbyResponse toDto(Hobby entity);

    Hobby toEntity(HobbyRequest request);

    List<HobbyResponse> toDtoList(List<Hobby> entities);
}
