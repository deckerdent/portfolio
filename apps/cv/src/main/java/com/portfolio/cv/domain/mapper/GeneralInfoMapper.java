package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.GeneralInfo;
import com.portfolio.cv.model.GeneralInfoRequest;
import com.portfolio.cv.model.GeneralInfoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface GeneralInfoMapper {

    GeneralInfoResponse toDto(GeneralInfo entity);

    GeneralInfo toEntity(GeneralInfoRequest request);
}
