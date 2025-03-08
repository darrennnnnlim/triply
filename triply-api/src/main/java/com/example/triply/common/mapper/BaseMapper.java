package com.example.triply.common.mapper;

import com.example.triply.common.audit.Auditable;
import com.example.triply.common.dto.MutableDTO;

import java.util.List;

public interface BaseMapper<E extends Auditable, D extends MutableDTO> {

    D toDto(E entity);
    E toEntity(D dto);

    default void mapAuditFieldsToDto(E entity, D dto) {
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
    }

    default void mapAuditFieldsToEntity(D dto, E entity) {
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(dto.getLastModifiedDate());
    }

    default List<D> toDto(List<E> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    default List<E> toEntity(List<D> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

}
