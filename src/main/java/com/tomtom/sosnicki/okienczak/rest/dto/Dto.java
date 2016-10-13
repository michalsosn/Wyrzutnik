package com.tomtom.sosnicki.okienczak.rest.dto;

import com.vividsolutions.jts.geom.GeometryFactory;

public interface Dto<E> {
    E toEntity(GeometryFactory geometryFactory);
    void applyToEntity(GeometryFactory geometryFactory, E entity);
    void copyFromEntity(E entity);
}
