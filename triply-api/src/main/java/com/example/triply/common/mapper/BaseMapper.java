package com.example.triply.common.mapper;

import java.util.List;

public interface BaseMapper<E, D> {

    D toDto(E entity);
    E toEntity(D dto);
    List<D> toDto(List<E> entities);
    List<E> toEntity(List<D> dto);

}
