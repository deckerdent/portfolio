package com.portfolio.cv.domain.mapper;

import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Shared MapStruct utility mapper providing conversions between Java time types
 * and OpenAPI-generator-produced types (OffsetDateTime, JsonNullable).
 * <p>
 * Other mappers declare {@code uses = DateMapper.class} so MapStruct picks up
 * these methods automatically for matching types.
 */
@Mapper(componentModel = "spring")
public abstract class DateMapper {

    /** Entity LocalDateTime → DTO OffsetDateTime (UTC). */
    public OffsetDateTime toOffsetDateTime(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(ZoneOffset.UTC);
    }

    /** DTO OffsetDateTime → entity LocalDateTime. */
    public LocalDateTime toLocalDateTime(OffsetDateTime odt) {
        return odt == null ? null : odt.toLocalDateTime();
    }

    /**
     * Nullable LocalDate → JsonNullable (wraps null values too, so the JSON
     * serializer emits an explicit {@code null} rather than omitting the field).
     */
    public JsonNullable<LocalDate> toJsonNullable(LocalDate date) {
        return JsonNullable.of(date);
    }

    /** JsonNullable → nullable LocalDate. */
    public LocalDate fromJsonNullable(JsonNullable<LocalDate> jn) {
        return (jn != null && jn.isPresent()) ? jn.get() : null;
    }
}
