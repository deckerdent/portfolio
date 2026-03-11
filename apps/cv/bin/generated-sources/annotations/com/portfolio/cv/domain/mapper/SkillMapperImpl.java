package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Skill;
import com.portfolio.cv.model.SkillRequest;
import com.portfolio.cv.model.SkillResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-11T16:42:43+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SkillMapperImpl implements SkillMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public SkillResponse toDto(Skill entity) {
        if ( entity == null ) {
            return null;
        }

        SkillResponse skillResponse = new SkillResponse();

        skillResponse.setId( entity.getId() );
        skillResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        skillResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        skillResponse.setTitle( entity.getTitle() );
        skillResponse.setLevel( entity.getLevel() );

        return skillResponse;
    }

    @Override
    public Skill toEntity(SkillRequest request) {
        if ( request == null ) {
            return null;
        }

        Skill.SkillBuilder skill = Skill.builder();

        skill.level( request.getLevel() );
        skill.title( request.getTitle() );

        return skill.build();
    }

    @Override
    public List<SkillResponse> toDtoList(List<Skill> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SkillResponse> list = new ArrayList<SkillResponse>( entities.size() );
        for ( Skill skill : entities ) {
            list.add( toDto( skill ) );
        }

        return list;
    }
}
