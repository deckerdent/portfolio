package com.portfolio.cv.domain.mapper;

import com.portfolio.cv.domain.model.Hobby;
import com.portfolio.cv.model.HobbyRequest;
import com.portfolio.cv.model.HobbyResponse;
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
public class HobbyMapperImpl implements HobbyMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public HobbyResponse toDto(Hobby entity) {
        if ( entity == null ) {
            return null;
        }

        HobbyResponse hobbyResponse = new HobbyResponse();

        hobbyResponse.setId( entity.getId() );
        hobbyResponse.setCreatedAt( dateMapper.toOffsetDateTime( entity.getCreatedAt() ) );
        hobbyResponse.setUpdatedAt( dateMapper.toOffsetDateTime( entity.getUpdatedAt() ) );
        hobbyResponse.setName( entity.getName() );

        return hobbyResponse;
    }

    @Override
    public Hobby toEntity(HobbyRequest request) {
        if ( request == null ) {
            return null;
        }

        Hobby.HobbyBuilder hobby = Hobby.builder();

        hobby.name( request.getName() );

        return hobby.build();
    }

    @Override
    public List<HobbyResponse> toDtoList(List<Hobby> entities) {
        if ( entities == null ) {
            return null;
        }

        List<HobbyResponse> list = new ArrayList<HobbyResponse>( entities.size() );
        for ( Hobby hobby : entities ) {
            list.add( toDto( hobby ) );
        }

        return list;
    }
}
